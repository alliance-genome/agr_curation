import { useRef } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Toast } from 'primereact/toast';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import { useAGMReducer } from './useAGMReducer';
import { processErrors } from './utils';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { AGMForm, AGM_CREATE_TOGGLEABLE_FIELDS } from './AGMForm';

/**
 * Pop-up for creating a new AGM, mounted inline on the AGM table (the disease annotation create
 * dialog's pattern) rather than a routed page. Fields match the AGM detail page - AGMForm is
 * reused here with mode="create" to drop the server-assigned identifiers and mark Sub Type/Taxon
 * required, the same way the Allele create page reuses AlleleForm.
 *
 * @param {Object} props
 * @param {boolean} props.visible
 * @param {() => void} props.onHide
 */
export const AffectedGenomicModelCreateDialog = ({ visible, onHide }) => {
	const { agmState, agmDispatch } = useAGMReducer();
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'AGMCreate',
		AGM_CREATE_TOGGLEABLE_FIELDS
	);
	const agmService = new AffectedGenomicModelService();
	const queryClient = useQueryClient();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const { isPending, mutate } = useMutation({
		mutationFn: (agm) => agmService.createAGM(agm),
	});

	const resetForm = () => {
		agmDispatch({ type: 'RESET' });
	};

	const closeDialog = () => {
		resetForm();
		onHide();
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
			onSuccess: () => {
				// The table's search query is keyed on 'AffectedGenomicModels' (getDefaultTableState's
				// tableKeyName), so this is what brings the new row into view without a manual refetch.
				queryClient.invalidateQueries({ queryKey: ['AffectedGenomicModels'] });
				toastSuccess.current.show({ severity: 'success', summary: 'Successful', detail: 'AGM Created' });

				if (closeAfterSubmit) {
					closeDialog();
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
		closeDialog();
	};

	const dialogHeader = (
		<Splitter className="bg-primary-reverse border-none" gutterSize={0}>
			<SplitterPanel size={45} className="flex align-items-center justify-content-start ml-3 py-2">
				<h4 className="m-0">Add AGM</h4>
			</SplitterPanel>
			<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-3 py-2">
				<FormFieldVisibilityMenu
					toggleableFields={AGM_CREATE_TOGGLEABLE_FIELDS}
					visibleFields={visibleFields}
					setVisibleFields={setVisibleFields}
					showAllFields={showAllFields}
				/>
			</SplitterPanel>
		</Splitter>
	);

	const dialogFooter = (
		<Splitter className="border-none" gutterSize={0}>
			<SplitterPanel size={50} className="flex justify-content-start py-2">
				<Button label="Clear" icon="pi pi-undo" className="p-button-text" onClick={handleClear} />
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={handleCancel} />
			</SplitterPanel>
			<SplitterPanel size={50} className="flex justify-content-end py-2">
				<Button
					label="Save & Close"
					icon="pi pi-check"
					severity="success"
					disabled={isPending}
					onClick={handleSubmitAndClose}
				/>
				<Button
					label="Save & Add Another"
					icon="pi pi-check"
					severity="success"
					className="ml-2"
					disabled={isPending}
					onClick={handleSubmitAndAddAnother}
				/>
			</SplitterPanel>
		</Splitter>
	);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<Dialog
				visible={visible}
				header={dialogHeader}
				footer={dialogFooter}
				position="top"
				modal
				maximizable
				className="p-fluid w-9"
				onHide={closeDialog}
			>
				<ErrorBoundary>
					<AGMForm state={agmState} dispatch={agmDispatch} isVisible={isVisible} mode="create" />
				</ErrorBoundary>
			</Dialog>
		</>
	);
};
