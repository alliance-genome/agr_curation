import { useRef, useEffect, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { VariantService } from '../../service/VariantService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { useVariantReducer } from './useVariantReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { buildSavePayload, processErrors } from './utils';
import { getIdentifier } from '../../utils/utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { VariantForm, VARIANT_DETAIL_TOGGLEABLE_FIELDS } from './VariantForm';
import { DuplicateVariantButton, NewVariantButton } from './NewVariantButton';
import { useDeleteOrDeprecateDialogs } from '../../components/DeleteOrDeprecateDialogs';

export default function VariantDetailPage() {
	const { identifier } = useParams();
	const navigate = useNavigate();
	const { variantState, variantDispatch } = useVariantReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'VariantDetail',
		VARIANT_DETAIL_TOGGLEABLE_FIELDS
	);
	const variantService = useMemo(() => new VariantService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const savedVariant = useRef(null);

	const { isPending: getRequestIsLoading, data: variantQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => variantService.getVariant(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (variantQueryData) {
			savedVariant.current = variantQueryData?.data?.entity;
			variantDispatch({ type: 'SET', value: variantQueryData?.data?.entity });
		}
	}, [variantQueryData, variantDispatch]);

	const {
		isPending: variantPutRequestIsLoading,
		mutate: variantMutate,
		mutateAsync: saveVariant,
	} = useMutation({
		mutationFn: (variant) => {
			return variantService.saveVariant(variant);
		},
	});

	const handleSubmit = async (event) => {
		event.preventDefault();
		variantDispatch({
			type: 'SUBMIT',
		});

		variantMutate(buildSavePayload(variantState.variant), {
			onSuccess: (result) => {
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Variant Saved' });
				savedVariant.current = result?.data?.entity;
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

	/**
	 * Deprecates the variant as last saved, so the form's unsaved edits are not written with it. They stay on
	 * the form, which then shows the variant as obsolete; a refused deprecation leaves the form as it was.
	 */
	const deprecateVariant = async () => {
		try {
			const result = await saveVariant(buildSavePayload({ ...savedVariant.current, obsolete: true }));
			savedVariant.current = result?.data?.entity;
		} catch (error) {
			const data = error?.response?.data;
			toastError.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Variant not deprecated: ',
					detail:
						data?.errorMessage ??
						(error?.response ? `${error.response.status} ${error.response.statusText}`.trim() : error?.message),
					sticky: false,
				},
			]);
			return;
		}
		variantDispatch({ type: 'EDIT', field: 'obsolete', value: true });
		toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'Variant Deprecated' });
	};

	const deleteVariant = async (id, variant) => {
		const result = await variantService.deleteVariant(variant);
		if (result.isError) {
			toastError.current.show([
				{ life: 7000, severity: 'error', summary: `Could not delete variant ${getIdentifier(variant)}`, sticky: false },
			]);
			return result.message ? result.message : null;
		}
		navigate('/variants');
		return null;
	};

	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption: true,
		onDelete: deleteVariant,
		onDeprecate: deprecateVariant,
	});

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
			<LoadingOverlay isLoading={!!variantPutRequestIsLoading} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={40} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Variant"
								entity={variantState.variant}
								label={variantState.variant?.variantType?.name}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={VARIANT_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-start gap-2 pl-2 py-3">
							<Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} />
							<NewVariantButton className="p-button-text" />
							<DuplicateVariantButton
								className="p-button-text"
								sourceIdentifier={variantState.variant?.curie || getIdentifier(variantState.variant) || identifier}
							/>
							<Button
								label="Delete"
								icon="pi pi-trash"
								className="p-button-text"
								disabled={!variantState.variant?.id}
								onClick={() => openDeleteOrDeprecateDialog(variantState.variant.id, variantState.variant)}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<VariantForm state={variantState} dispatch={variantDispatch} isVisible={isVisible} />
				{deleteOrDeprecateDialogs}
			</ErrorBoundary>
		</>
	);
}
