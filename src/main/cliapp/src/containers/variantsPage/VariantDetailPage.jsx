import { useRef, useEffect, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { VariantService } from '../../service/VariantService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useVariantReducer } from './useVariantReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { processErrors } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { VariantForm, VARIANT_DETAIL_TOGGLEABLE_FIELDS } from './VariantForm';

export default function VariantDetailPage() {
	const { identifier } = useParams();
	const { variantState, variantDispatch } = useVariantReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'VariantDetail',
		VARIANT_DETAIL_TOGGLEABLE_FIELDS
	);
	const variantService = useMemo(() => new VariantService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const { isPending: getRequestIsLoading, data: variantQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => variantService.getVariant(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (variantQueryData) {
			variantDispatch({ type: 'SET', value: variantQueryData?.data?.entity });
		}
	}, [variantQueryData, variantDispatch]);

	const { isPending: variantPutRequestIsLoading, mutate: variantMutate } = useMutation({
		mutationFn: (variant) => {
			return variantService.saveVariant(variant);
		},
	});

	const handleSubmit = async (event) => {
		event.preventDefault();
		variantDispatch({
			type: 'SUBMIT',
		});

		variantMutate(variantState.variant, {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Variant Saved' });
				variantDispatch({ type: 'SET', value: result?.data?.entity });
			},
			onError: (error) => {
				let message;
				const data = error?.response?.data;

				if (data?.errorMessage) {
					message = data.errorMessage;
				} else {
					//toast will still display even if 500 error and no errorMessages
					message = `${error.response.status} ${error.response.statusText}`;
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

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	const headerText = () => {
		let prefix = 'Variant: ';
		if (variantState.variant?.variantType?.name && variantState.variant?.primaryExternalId) {
			return `${prefix} ${variantState.variant.variantType.name} (${variantState.variant.primaryExternalId})`;
		}
		if (variantState.variant?.primaryExternalId) {
			return `${prefix} ${variantState.variant.primaryExternalId}`;
		}
		if (variantState.variant?.curie) {
			return `${prefix} ${variantState.variant.curie}`;
		}
		return 'Variant Detail Page';
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!variantPutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1 dangerouslySetInnerHTML={{ __html: headerText() }} />
						</SplitterPanel>
						<SplitterPanel size={35} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={VARIANT_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={20} className="flex align-items-center justify-content-start gap-2 py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<VariantForm state={variantState} dispatch={variantDispatch} isVisible={isVisible} />
			</ErrorBoundary>
		</>
	);
}
