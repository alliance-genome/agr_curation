import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ValidationService } from '../../../service/ValidationService';
import { Endpoints } from '../../../constants/Endpoints';
import { CrossReferencesTable } from './CrossReferencesTable';
import {
	applyCrossReferenceFieldChange,
	buildNewCrossReference,
	findMissingRequiredFields,
	seedResourceDescriptor,
	seedResourceDescriptors,
	stripForValidation,
	stripUiFields,
} from './utils';
import { addDataKey } from '../utils';

/**
 * Stages an allele's cross references into its table row. Nothing is written here - the row's own
 * save issues the PUT, so a row edit that is cancelled discards these edits with the rest.
 */
export const CrossReferencesEditDialog = ({
	originalCrossReferencesData,
	setOriginalCrossReferencesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalCrossReferences, isInEdit, dialog, rowIndex, mainRowProps } = originalCrossReferencesData;
	const [localCrossReferences, setLocalCrossReferences] = useState([]);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = async () => {
		// Each stored row takes the descriptor nested in its page straight away, so it counts as having one
		// before the full descriptor, with its pages, is read.
		const clonedCrossReferences = (structuredClone(originalCrossReferences) ?? []).map(seedResourceDescriptor);
		clonedCrossReferences.forEach(addDataKey);

		setEditingRows(Object.fromEntries(clonedCrossReferences.map((crossReference) => [crossReference.dataKey, true])));
		setErrorMessages({});
		setLocalCrossReferences(clonedCrossReferences);

		// Rendered before this resolves so the table is never blank, then merged in by dataKey rather
		// than replaced, so a descriptor arriving late cannot discard an edit made while it was loading.
		const seededCrossReferences = await seedResourceDescriptors(clonedCrossReferences);
		const seededByDataKey = new Map(
			seededCrossReferences.map((crossReference) => [crossReference.dataKey, crossReference])
		);

		setLocalCrossReferences((current) =>
			current.map((crossReference) => {
				const seeded = seededByDataKey.get(crossReference.dataKey);
				return seeded ? { ...crossReference, resourceDescriptor: seeded.resourceDescriptor } : crossReference;
			})
		);
	};

	const onRowEditChange = () => {
		return null;
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalCrossReferencesData((previous) => ({ ...previous, dialog: false }));
		setLocalCrossReferences([]);
	};

	const onFieldChange = (dataKey, field, value) => {
		setLocalCrossReferences((previous) =>
			previous.map((crossReference) =>
				crossReference.dataKey === dataKey
					? applyCrossReferenceFieldChange(crossReference, field, value)
					: crossReference
			)
		);
	};

	const createNewCrossReferenceHandler = () => {
		const newCrossReference = buildNewCrossReference();
		setEditingRows((previous) => ({ ...previous, [newCrossReference.dataKey]: true }));
		setLocalCrossReferences((previous) => [...previous, newCrossReference]);
	};

	const handleDeleteCrossReference = (event, dataKey) => {
		setLocalCrossReferences((previous) => previous.filter((crossReference) => crossReference.dataKey !== dataKey));
	};

	const saveDataHandler = () => {
		setErrorMessages({});

		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(localCrossReferences.map(stripUiFields));
		}

		// Keyed to match both DialogTriggerEditor's errorField and the key the API reports cross
		// reference errors under, so a later save error replaces this warning on the same cell.
		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		errorMessagesCopy[rowIndex] = {
			...errorMessagesCopy[rowIndex],
			crossReferences: { severity: 'warn', message: 'Pending Edits!' },
		};
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalCrossReferencesData((previous) => ({ ...previous, dialog: false }));
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			// Empty required fields are caught in the browser before any row goes to the API, which never
			// reports a missing resource descriptor since it is not stored on a cross reference.
			const newErrorMessages = findMissingRequiredFields(localCrossReferences);
			let hasErrors = Object.keys(newErrorMessages).length > 0;

			if (!hasErrors) {
				for (const crossReference of localCrossReferences) {
					const result = await validationService.validate(
						Endpoints.Entity.CROSS_REFERENCE,
						stripForValidation(crossReference)
					);

					if (result.isError) {
						hasErrors = true;
						newErrorMessages[crossReference.dataKey] = {};
						if (result.data) {
							Object.keys(result.data).forEach((field) => {
								newErrorMessages[crossReference.dataKey][field] = {
									severity: 'error',
									message: result.data[field],
								};
							});
						}
					}
				}
			}

			if (hasErrors) {
				setErrorMessages(newErrorMessages);
				toast_topright.current.show([
					{
						life: 7000,
						severity: 'error',
						summary: 'Validation error',
						detail: 'Please fix validation errors before saving',
						sticky: false,
					},
				]);
			} else {
				saveDataHandler();
			}
		} finally {
			setIsValidating(false);
		}
	};

	const footerTemplate = () => {
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Cross Reference" icon="pi pi-plus" onClick={createNewCrossReferenceHandler} />
				<Button
					label="Keep Edits"
					icon={isValidating ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
					onClick={validateAndSave}
					disabled={isValidating}
				/>
			</div>
		);
	};

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-8"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Cross References</h3>
				<CrossReferencesTable
					crossReferences={localCrossReferences}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					errorMessages={errorMessages}
					deletionHandler={handleDeleteCrossReference}
					onFieldChange={onFieldChange}
					showObsolete
				/>
			</Dialog>
		</div>
	);
};
