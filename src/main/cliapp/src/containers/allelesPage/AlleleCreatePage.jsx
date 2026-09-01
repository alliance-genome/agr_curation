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
import {
	buildCreatePayload,
	processErrors,
	validateRequiredAutosuggestField,
	validateRequiredCreateFields,
} from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AlleleForm, ALLELE_CREATE_TOGGLEABLE_FIELDS } from './AlleleForm';

export default function AlleleCreatePage() {
	const navigate = useNavigate();
	const { alleleState, alleleDispatch } = useAlleleReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AlleleCreate',
		ALLELE_CREATE_TOGGLEABLE_FIELDS
	);
	const alleleService = new AlleleService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const { isPending: allelePostRequestIsLoading, mutate: alleleMutate } = useMutation({
		mutationFn: (allele) => {
			return alleleService.createAllele(allele);
		},
	});

	const handleSubmit = (event, closeAfterSubmit) => {
		event.preventDefault();
		alleleDispatch({ type: 'SUBMIT' });

		// Required field messages are dispatched first so a failed check replaces any messages
		// left over from an earlier save.
		const areRequiredFieldErrors = validateRequiredCreateFields(alleleState.allele, alleleDispatch);

		const areUiErrors = validateRequiredAutosuggestField(
			alleleState.allele.alleleGeneAssociations,
			alleleState.entityStates.alleleGeneAssociations.errorMessages,
			alleleDispatch,
			'alleleGeneAssociations',
			'alleleGeneAssociationObject'
		);

		if (areRequiredFieldErrors || areUiErrors) return;

		alleleMutate(buildCreatePayload(alleleState.allele), {
			onSuccess: (result) => {
				const allele = result?.data?.entity;
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Allele Created' });

				if (closeAfterSubmit) {
					navigate(`/allele/${getIdentifier(allele)}`);
				} else {
					alleleDispatch({ type: 'RESET' });
				}
			},
			onError: (error) => {
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
			},
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
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/alleles');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!allelePostRequestIsLoading} />
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
					<AlleleForm state={alleleState} dispatch={alleleDispatch} isVisible={isVisible} mode="create" />
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
