import { useRef } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import { useAGMReducer } from './useAGMReducer';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { processErrors } from './utils';
import { getIdentifier } from '../../utils/utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AGMForm, AGM_CREATE_TOGGLEABLE_FIELDS } from './AGMForm';
import { useDuplicateAgmSource } from './useDuplicateAgmSource';

export default function AffectedGenomicModelCreatePage() {
	const navigate = useNavigate();
	const { agmState, agmDispatch } = useAGMReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AGMCreate',
		AGM_CREATE_TOGGLEABLE_FIELDS
	);
	const agmService = new AffectedGenomicModelService();
	const queryClient = useQueryClient();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { isLoading: duplicateSourceIsLoading } = useDuplicateAgmSource({ agmDispatch, toastError });

	const { isPending, mutate } = useMutation({
		mutationFn: (agm) => agmService.createAGM(agm),
	});

	const resetForm = () => {
		agmDispatch({ type: 'RESET' });
	};

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
			processErrors(data, agmDispatch, agmState.agm);
		} catch (e) {
			console.error(e);
		}
	};

	const handleSubmit = (event, closeAfterSubmit) => {
		event.preventDefault();
		agmDispatch({ type: 'SUBMIT' });

		// Jackson's polymorphic BiologicalEntity deserialization requires this discriminator on
		// create; the update path doesn't need it because the entity already exists with a type.
		const payload = { ...agmState.agm, type: 'AffectedGenomicModel' };

		// The reducer's initial taxon is {curie: ''}, not null, so a curator who leaves Taxon
		// untouched would otherwise submit a non-null object the server's required-field check
		// doesn't catch (it only rejects a null taxon) - same guard Allele's create page uses.
		if (!payload.taxon?.curie) {
			delete payload.taxon;
		}

		mutate(payload, {
			onSuccess: (result) => {
				// The table's search query is keyed on 'AffectedGenomicModels' (getDefaultTableState's
				// tableKeyName), so this is what brings the new row into view without a manual refetch.
				queryClient.invalidateQueries({ queryKey: ['AffectedGenomicModels'] });
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'AGM Created' });

				if (closeAfterSubmit) {
					resetForm();
					// Same as Allele/Variant's create pages: land on the new record's detail page
					// rather than just closing. A curator-created AGM has no MOD identifier, so this
					// is its minted AGRKB curie.
					navigate(`/agm/${getIdentifier(result?.data?.entity)}`);
				} else {
					resetForm();
				}
			},
			onError: showSaveError,
		});
	};

	const handleSubmitAndClose = (event) => handleSubmit(event, true);
	const handleSubmitAndAddAnother = (event) => handleSubmit(event, false);

	const handleClear = (event) => {
		event.preventDefault();
		resetForm();
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/agms');
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<LoadingOverlay isLoading={!!isPending} />
			<LoadingOverlay isLoading={duplicateSourceIsLoading} message="Loading AGM to duplicate..." />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add AGM</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={AGM_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<AGMForm state={agmState} dispatch={agmDispatch} isVisible={isVisible} mode="create" />
				</div>
				<StickyFooter>
					<Splitter className="bg-primary-reverse border-none" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start ml-5 py-3">
							<Button label="Clear" icon="pi pi-undo" className="p-button-text" onClick={handleClear} />
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={handleCancel} />
						</SplitterPanel>
						<SplitterPanel size={50} className="flex justify-content-end mr-5 py-3">
							<Button label="Save & Close" icon="pi pi-check" severity="success" onClick={handleSubmitAndClose} />
							<Button
								label="Save & Add Another"
								icon="pi pi-check"
								severity="success"
								className="ml-2"
								onClick={handleSubmitAndAddAnother}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyFooter>
			</ErrorBoundary>
		</>
	);
}
