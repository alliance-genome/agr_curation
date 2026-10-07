import { useRef, useEffect } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useAlleleReducer } from './useAlleleReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { FitTextHeading } from '../../components/FitTextHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { validateRequiredAutosuggestField, processErrors, getPendingSingleValueFields } from './utils';
import { getIdentifier } from '../../utils/utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_DETAIL_TOGGLEABLE_FIELDS } from './AlleleForm';
import { DuplicateAlleleButton, NewAlleleButton } from './NewAlleleButton';
import { useAlleleCrossReferences } from './crossReferences/useAlleleCrossReferences';
import { SubResourcesProvider } from '../../components/SubResourcesContext';
import { useDeleteOrDeprecateDialogs } from '../../components/DeleteOrDeprecateDialogs';

export default function AlleleDetailPage() {
	const { identifier } = useParams();
	const navigate = useNavigate();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AlleleDetail',
		ALLELE_DETAIL_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const crossReferences = useAlleleCrossReferences(alleleState.allele?.id);
	// The allele as the API last returned it, without the form's unsaved edits.
	const savedAllele = useRef(null);

	const { isPending: getRequestIsLoading, data: alleleQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => alleleService.getAllele(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (alleleQueryData) {
			savedAllele.current = alleleQueryData?.data?.entity;
			alleleDispatch({ type: 'SET', value: alleleQueryData?.data?.entity });
		}
	}, [alleleQueryData, alleleDispatch]);

	const { isPending: allelePutRequestIsLoading, mutateAsync: saveAllele } = useMutation({
		mutationFn: (allele) => {
			return alleleService.saveAlleleDetail(allele);
		},
	});

	/**
	 * Marks a rejected allele's field errors on the form.
	 *
	 * @param {Object} error the rejected request's error
	 * @returns {string} the message describing the rejection
	 */
	const reportAlleleErrors = (error) => {
		const data = error?.response?.data;

		try {
			processErrors(data, alleleDispatch, alleleState.allele);
		} catch (processingError) {
			console.error(processingError);
		}

		//toast will still display even if 500 error and no errorMessages
		return data.errorMessage ? data.errorMessage : `${error.response.status} ${error.response.statusText}`;
	};

	const handleSubmit = async (event) => {
		event.preventDefault();
		alleleDispatch({
			type: 'SUBMIT',
		});

		const areUiErrors = validateRequiredAutosuggestField(
			alleleState.allele.alleleGeneAssociations,
			alleleState.entityStates.alleleGeneAssociations.errorMessages,
			alleleDispatch,
			'alleleGeneAssociations',
			'alleleGeneAssociationObject'
		);

		if (areUiErrors) return;

		// A cleared taxon is held as one with a blank curie, which the API resolves to none without reporting
		// it missing. Sent without a taxon, the allele is reported as needing one.
		const allelePayload = alleleState.allele.taxon?.curie
			? alleleState.allele
			: { ...alleleState.allele, taxon: undefined };

		let alleleResult = null;
		let alleleError = null;
		try {
			alleleResult = await saveAllele(allelePayload);
		} catch (error) {
			alleleError = error;
		}
		if (!alleleError) {
			savedAllele.current = alleleResult?.data?.entity;
			alleleDispatch({ type: 'SET', value: alleleResult?.data?.entity });
		}

		// The cross references are written only when the section has been edited, and then as the whole
		// list, so saving the allele alone leaves the stored ones as they are. They are written whether or
		// not the allele saved, so the errors of both are reported together.
		const crossReferencesOutcome = crossReferences.isDirty ? await crossReferences.save() : null;

		if (alleleError) {
			const alleleMessage = reportAlleleErrors(alleleError);

			if (!crossReferencesOutcome) {
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: alleleMessage, sticky: false },
				]);
			} else if (crossReferencesOutcome.isSuccess) {
				toastError.current.show([
					{
						life: 10000,
						severity: 'error',
						summary: 'Allele not saved: ',
						detail: `${alleleMessage}. The cross references were saved.`,
						sticky: false,
					},
				]);
			} else {
				toastError.current.show([
					{ life: 10000, severity: 'error', summary: 'Allele not saved: ', detail: alleleMessage, sticky: false },
					{
						life: 10000,
						severity: 'error',
						summary: 'Cross references not saved: ',
						detail: crossReferencesOutcome.message,
						sticky: false,
					},
				]);
			}
			return;
		}

		if (crossReferencesOutcome && !crossReferencesOutcome.isSuccess) {
			toastError.current.show([
				{
					life: 10000,
					severity: 'error',
					summary: 'Cross references not saved: ',
					detail: `${crossReferencesOutcome.message}. The allele's other changes were saved.`,
					sticky: false,
				},
			]);
			return;
		}

		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Saved' });
	};

	/**
	 * Deprecates the allele as last saved, so the form's unsaved edits and cross references are not written
	 * with it. They stay on the form, which then shows the allele as obsolete; a refused deprecation leaves the
	 * form as it was.
	 */
	const deprecateAllele = async () => {
		try {
			const result = await saveAllele({ ...savedAllele.current, obsolete: true });
			savedAllele.current = result?.data?.entity;
		} catch (error) {
			const data = error?.response?.data;
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Allele not deprecated: ',
					detail:
						data?.errorMessage ??
						(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message),
					sticky: false,
				},
			]);
			return;
		}
		alleleDispatch({ type: 'EDIT', field: 'obsolete', value: true });
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Deprecated' });
	};

	const deleteAllele = async (id, allele) => {
		const result = await alleleService.deleteAllele(allele);
		if (result.isError) {
			toastError.current.show([
				{ life: 7000, severity: 'error', summary: `Could not delete allele ${getIdentifier(allele)}`, sticky: false },
			]);
			return result.message ? result.message : null;
		}
		navigate('/alleles');
		return null;
	};

	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption: true,
		onDelete: deleteAllele,
		onDeprecate: deprecateAllele,
	});

	const pendingFields = getPendingSingleValueFields(alleleState.allele, savedAllele.current);
	const hasPendingEdits = pendingFields.size > 0 || alleleState.hasOtherPendingEdits || crossReferences.isDirty;

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const headerText = () => {
		let prefix = 'Allele: ';
		const alleleIdentifier = getIdentifier(alleleState.allele);
		if (alleleState.allele?.alleleSymbol?.displayText && alleleIdentifier) {
			return `${prefix} ${alleleState.allele.alleleSymbol.displayText} (${alleleIdentifier})`;
		}
		if (alleleIdentifier) {
			return `${prefix} ${alleleIdentifier}`;
		}
		return 'Allele Detail Page';
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePutRequestIsLoading || crossReferences.isSaving} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={40} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<FitTextHeading html={headerText()} />
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={ALLELE_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button
								label="Save"
								icon="pi pi-check"
								severity="success"
								onClick={handleSubmit}
								disabled={!hasPendingEdits}
							/>
							<NewAlleleButton className="p-button-text" />
							<DuplicateAlleleButton
								className="p-button-text"
								sourceIdentifier={alleleState.allele?.curie || getIdentifier(alleleState.allele) || identifier}
							/>
							<Button
								label="Delete"
								icon="pi pi-trash"
								className="p-button-text"
								disabled={!alleleState.allele?.id}
								onClick={() => openDeleteOrDeprecateDialog(alleleState.allele.id, alleleState.allele)}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<SubResourcesProvider value={{ crossReferences }}>
					<AlleleForm
						state={alleleState}
						dispatch={alleleDispatch}
						isVisible={isVisible}
						pendingFields={pendingFields}
					/>
				</SubResourcesProvider>
				{deleteOrDeprecateDialogs}
			</ErrorBoundary>
		</>
	);
}
