import { useRef, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { VariantService } from '../../service/VariantService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useVariantReducer } from './useVariantReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { getIdentifier } from '../../utils/utils';
import { buildCreatePayload, processErrors } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { VariantForm, VARIANT_CREATE_TOGGLEABLE_FIELDS } from './VariantForm';

export default function VariantCreatePage() {
	const navigate = useNavigate();
	const { variantState, variantDispatch } = useVariantReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'VariantCreate',
		VARIANT_CREATE_TOGGLEABLE_FIELDS
	);
	const variantService = useMemo(() => new VariantService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const { isPending: variantPostRequestIsLoading, mutate: variantMutate } = useMutation({
		mutationFn: (variant) => {
			return variantService.createVariant(variant);
		},
	});

	const handleSubmit = (event, closeAfterSubmit) => {
		event.preventDefault();
		variantDispatch({ type: 'SUBMIT' });

		variantMutate(buildCreatePayload(variantState.variant), {
			onSuccess: (result) => {
				const variant = result?.data?.entity;
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Variant Created' });

				if (closeAfterSubmit) {
					navigate(`/variant/${getIdentifier(variant)}`);
				} else {
					variantDispatch({ type: 'RESET' });
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
					processErrors(data, variantDispatch, variantState.variant);
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
		variantDispatch({ type: 'RESET' });
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/variants');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!variantPostRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Variant</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={VARIANT_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<VariantForm state={variantState} dispatch={variantDispatch} isVisible={isVisible} mode="create" />
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
