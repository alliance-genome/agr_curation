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
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { capitalize, processErrors, getPendingSingleValueFields } from './utils';
import { errorMessageOf, getIdentifier } from '../../utils/utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_DETAIL_TOGGLEABLE_FIELDS } from './AlleleForm';
import { DuplicateAlleleButton, NewAlleleButton } from './NewAlleleButton';
import { useAlleleSubResources } from './useAlleleSubResources';
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
	const {
		subResources,
		saveDirty: saveSubResources,
		isDirty: subResourcesAreDirty,
		isSaving: subResourcesAreSaving,
	} = useAlleleSubResources(alleleState.allele?.id);
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

		return errorMessageOf(error);
	};

	const handleSubmit = async (event) => {
		event.preventDefault();
		alleleDispatch({
			type: 'SUBMIT',
		});

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

		// The sub-resources are written whether or not the allele saved, so the errors of all of them are reported
		// together.
		const subResourceOutcomes = await saveSubResources();
		const savedLabels = subResourceOutcomes.filter(({ outcome }) => outcome.isSuccess).map(({ label }) => label);
		const failedSubResources = subResourceOutcomes.filter(({ outcome }) => !outcome.isSuccess);
		const failureToast = ({ label }, detail) => ({
			life: 10000,
			severity: 'error',
			summary: `${capitalize(label)} not saved: `,
			detail,
			sticky: false,
		});

		if (alleleError) {
			const alleleMessage = reportAlleleErrors(alleleError);

			if (subResourceOutcomes.length === 0) {
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: alleleMessage, sticky: false },
				]);
			} else {
				toastError.current.show([
					{
						life: 10000,
						severity: 'error',
						summary: 'Allele not saved: ',
						detail:
							savedLabels.length > 0
								? `${alleleMessage}. The ${new Intl.ListFormat('en').format(savedLabels)} were saved.`
								: alleleMessage,
						sticky: false,
					},
					...failedSubResources.map((failure) => failureToast(failure, failure.outcome.message)),
				]);
			}
			return;
		}

		if (failedSubResources.length > 0) {
			toastError.current.show(
				failedSubResources.map((failure) =>
					failureToast(failure, `${failure.outcome.message}. The allele's other changes were saved.`)
				)
			);
			return;
		}

		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Saved' });
	};

	/**
	 * Deprecates the allele as last saved, so the form's unsaved edits, including those to the lists written through
	 * their own sub-resources, are not written with it. They stay on the form, which then shows the allele as
	 * obsolete; a refused deprecation leaves the form as it was.
	 */
	const deprecateAllele = async () => {
		try {
			const result = await saveAllele({ ...savedAllele.current, obsolete: true });
			savedAllele.current = result?.data?.entity;
		} catch (error) {
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Allele not deprecated: ',
					detail: errorMessageOf(error),
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
	const hasPendingEdits = pendingFields.size > 0 || alleleState.hasOtherPendingEdits || subResourcesAreDirty;

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePutRequestIsLoading || subResourcesAreSaving} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={40} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Allele"
								entity={alleleState.allele}
								label={alleleState.allele?.alleleSymbol?.displayText}
							/>
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
				<SubResourcesProvider value={subResources}>
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
