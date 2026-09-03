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

	const { isPending: allelePutRequestIsLoading, mutate: alleleMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.saveAlleleDetail(allele);
		},
	});

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

		alleleMutate(alleleState.allele, {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Saved' });
				alleleDispatch({ type: 'SET', value: result?.data?.entity });
			},
			onError: (error) => {
				let message;
				const data = error?.response?.data;

				if (data.errorMessage) {
					message = error.response.data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
				}
				toastError.current.show([
					{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
				]);

				try {
					processErrors(data, alleleDispatch, alleleState.allele);
				} catch (e) {
					console.error(e);
				}
			},
		});
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
			<LoadingOverlay isLoading={!!allelePutRequestIsLoading} />
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
						<SplitterPanel size={20} className="flex justify-content-start py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} />
			</ErrorBoundary>
		</>
	);
}
