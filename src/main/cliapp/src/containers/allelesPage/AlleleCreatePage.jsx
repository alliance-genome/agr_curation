import { useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useAlleleReducer } from './useAlleleReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { getIdentifier } from '../../utils/utils';
import { buildCreatePayload, processErrors, validateRequiredAutosuggestField } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_CREATE_TOGGLEABLE_FIELDS } from './AlleleForm';
import { useAlleleCrossReferences } from './crossReferences/useAlleleCrossReferences';
import { SubResourcesProvider } from '../../components/SubResourcesContext';

export default function AlleleCreatePage() {
	const navigate = useNavigate();
	const { alleleState, alleleDispatch } = useAlleleReducer('create');
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AlleleCreate',
		ALLELE_CREATE_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const crossReferences = useAlleleCrossReferences();
	// Held so that saving again after the cross references failed retries them against the allele that
	// already exists, rather than creating a second one.
	const createdAllele = useRef(null);

	const { isPending: allelePostRequestIsLoading, mutate: alleleMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.createAllele(allele);
		},
	});

	// Saving again once the allele exists updates it rather than creating another. updateDetail rather
	// than the plain allele endpoint, because that one manages cross references from its payload and
	// this page never carries them there - it would read their absence as an instruction to clear them.
	const { isPending: allelePutRequestIsLoading, mutate: alleleUpdateMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.saveAlleleDetail(allele);
		},
	});

	const showSaveError = (error) => {
		let message;
		const data = error?.response?.data;

		if (data?.errorMessage) {
			message = data.errorMessage;
		} else {
			//toast will still display even if 500 error and no errorMessages
			message = `${error.response?.status} ${error.response?.statusText}`;
		}
		toastError.current.show([
			{ life: 7000, severity: 'error', summary: 'Page error: ', detail: message, sticky: false },
		]);

		try {
			processErrors(data, alleleDispatch, alleleState.allele);
		} catch (e) {
			console.error(e);
		}
	};

	// Cross references are written through their own sub-resource, so create is two calls. The allele
	// exists by the time this runs, so a failure here leaves the work recoverable from its detail page
	// rather than lost - which is why it keeps the curator here instead of navigating away.
	const saveCrossReferences = async (allele, closeAfterSubmit) => {
		if (crossReferences.crossReferences.length > 0) {
			const outcome = await crossReferences.save(allele?.id);

			if (!outcome.isSuccess) {
				// The form takes on the saved allele, so the fields carry the ids that saving again needs
				// and an edit made while fixing the rows is sent as an update rather than dropped.
				alleleDispatch({ type: 'SET', value: allele });
				toastError.current.show([
					{
						life: 10000,
						severity: 'error',
						summary: 'Cross references not saved: ',
						detail: `${outcome.message}. Allele ${getIdentifier(allele)} was created - fix the rows and save again, or finish them on its detail page.`,
						sticky: false,
					},
				]);
				return;
			}
		}

		createdAllele.current = null;

		if (closeAfterSubmit) {
			navigate(`/allele/${getIdentifier(allele)}`);
		} else {
			alleleDispatch({ type: 'RESET' });
			crossReferences.setCrossReferences([]);
		}
	};

	const handleSubmit = (event, closeAfterSubmit) => {
		event.preventDefault();
		alleleDispatch({ type: 'SUBMIT' });

		const areUiErrors = validateRequiredAutosuggestField(
			alleleState.allele.alleleGeneAssociations,
			alleleState.entityStates.alleleGeneAssociations.errorMessages,
			alleleDispatch,
			'alleleGeneAssociations',
			'alleleGeneAssociationObject'
		);

		if (areUiErrors) return;

		if (createdAllele.current) {
			alleleUpdateMutate(alleleState.allele, {
				onSuccess: async (result) => {
					const allele = result?.data?.entity;
					createdAllele.current = allele;
					alleleDispatch({ type: 'SET', value: allele });

					await saveCrossReferences(allele, closeAfterSubmit);
				},
				onError: showSaveError,
			});
			return;
		}

		alleleMutate(buildCreatePayload(alleleState.allele), {
			onSuccess: async (result) => {
				const allele = result?.data?.entity;
				createdAllele.current = allele;
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Created' });

				await saveCrossReferences(allele, closeAfterSubmit);
			},
			onError: showSaveError,
		});
	};

	const handleSubmitAndClose = (event) => {
		handleSubmit(event, true);
	};

	const handleSubmitAndAddAnother = (event) => {
		handleSubmit(event, false);
	};

	const handleClear = (event) => {
		event.preventDefault();
		alleleDispatch({ type: 'RESET' });
		crossReferences.setCrossReferences([]);
		createdAllele.current = null;
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/alleles');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePostRequestIsLoading || !!allelePutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Allele</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={ALLELE_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<SubResourcesProvider value={{ crossReferences }}>
						<AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} mode="create" />
					</SubResourcesProvider>
				</div>
				<StickyFooter>
					<Splitter className="bg-primary-reverse border-none" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start ml-5 py-3">
							<Button label="Clear" icon="pi pi-undo" className="p-button-text" onClick={handleClear} />
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={handleCancel} />
						</SplitterPanel>
						<SplitterPanel size={50} className="flex justify-content-end mr-5 py-3">
							<Button
								label="Save & Close"
								icon="pi pi-check"
								className="p-button-text"
								onClick={handleSubmitAndClose}
							/>
							<Button
								label="Save & Add Another"
								icon="pi pi-check"
								className="p-button-text"
								onClick={handleSubmitAndAddAnother}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyFooter>
			</ErrorBoundary>
		</>
	);
}
