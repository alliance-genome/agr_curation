import { useRef, useEffect } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useAlleleReducer } from './useAlleleReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { validateRequiredAutosuggestField, processErrors } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_DETAIL_TOGGLEABLE_FIELDS } from './AlleleForm';
import { NewAlleleButton } from './NewAlleleButton';
import { useAlleleCrossReferences } from './crossReferences/useAlleleCrossReferences';
import { SubResourcesProvider } from '../../components/SubResourcesContext';

export default function AlleleDetailPage() {
	const { identifier } = useParams();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AlleleDetail',
		ALLELE_DETAIL_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const crossReferences = useAlleleCrossReferences(alleleState.allele?.id);

	const { isPending: getRequestIsLoading, data: alleleQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => alleleService.getAllele(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (alleleQueryData) {
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

		let alleleError = null;
		try {
			const result = await saveAllele(alleleState.allele);
			alleleDispatch({ type: 'SET', value: result?.data?.entity });
		} catch (error) {
			alleleError = error;
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

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const headerText = () => {
		let prefix = 'Allele: ';
		if (alleleState.allele?.alleleSymbol?.displayText && alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.alleleSymbol.displayText} (${alleleState.allele.primaryExternalId})`;
		}
		if (alleleState.allele?.primaryExternalId) {
			return `${prefix} ${alleleState.allele.primaryExternalId}`;
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
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1 dangerouslySetInnerHTML={{ __html: headerText() }} />
						</SplitterPanel>
						<SplitterPanel size={35} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={ALLELE_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={20} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button label="Save" icon="pi pi-check" severity="success" onClick={handleSubmit} />
							<NewAlleleButton className="p-button-text" />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<SubResourcesProvider value={{ crossReferences }}>
					<AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} />
				</SubResourcesProvider>
			</ErrorBoundary>
		</>
	);
}
