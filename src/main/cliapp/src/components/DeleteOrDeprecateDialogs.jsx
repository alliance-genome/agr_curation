import React, { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';

/**
 * The confirmation dialogs for deleting or deprecating an entity, and the dialog explaining a
 * deletion the API refused.
 *
 * @param {Object} options
 * @param {boolean} [options.deprecateOption] - true to offer deprecation, with deletion behind a
 *   confirmation checkbox, rather than a plain confirm-deletion dialog
 * @param {(id: *, entity: Object) => Promise<string|null>} options.onDelete - deletes the entity,
 *   resolving to the reason it could not be deleted, or null
 * @param {(entity: Object) => void} [options.onDeprecate] - deprecates the entity; required with deprecateOption
 * @returns {{ openDeleteOrDeprecateDialog: (id: *, entity: Object) => void, deleteOrDeprecateDialogs: JSX.Element }}
 *   a function opening the dialog for an entity, and the dialogs to render
 */
export const useDeleteOrDeprecateDialogs = ({ deprecateOption = false, onDelete, onDeprecate }) => {
	const [deleteDialog, setDeleteDialog] = useState(false);
	const [deprecateDialog, setDeprecateDialog] = useState(false);
	const [errorDialog, setErrorDialog] = useState(false);
	const [idToDelete, setIdToDelete] = useState(null);
	const [entityToDelete, setEntityToDelete] = useState(null);
	const [deletionErrorMessage, setDeletionErrorMessage] = useState(null);
	const [allowDelete, setAllowDelete] = useState(false);

	const openDeleteOrDeprecateDialog = (id, entity) => {
		let isPublic = true; // TODO: check field in entity when populated
		setIdToDelete(id);
		setEntityToDelete(entity);
		if (deprecateOption && isPublic) {
			setDeprecateDialog(true);
		} else {
			setDeleteDialog(true);
		}
	};

	const deleteOrDeprecate = async (deprecateOnly) => {
		setDeleteDialog(false);
		setDeprecateDialog(false);
		if (deprecateOnly) {
			onDeprecate(entityToDelete);
		} else {
			let _deletionErrorMessage = await onDelete(idToDelete, entityToDelete);
			setDeletionErrorMessage(_deletionErrorMessage);
			if (_deletionErrorMessage !== null) {
				setErrorDialog(true);
			}
		}
		setAllowDelete(false);
	};

	const hideDeprecateDialog = () => {
		setDeprecateDialog(false);
		setAllowDelete(false);
	};

	const hideDeleteDialog = () => {
		setDeleteDialog(false);
	};

	const hideErrorDialog = () => {
		setErrorDialog(false);
	};

	const deleteDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeleteDialog} />
				<Button label="Confirm" icon="pi pi-check" className="p-button-text" onClick={() => deleteOrDeprecate(false)} />
			</React.Fragment>
		);
	};

	const deprecateDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeprecateDialog} />
				<Button
					label="Deprecate"
					icon="pi pi-check"
					className="p-button-text"
					onClick={() => deleteOrDeprecate(true)}
				/>
				<Button
					label="Delete"
					icon="pi pi-check"
					className="p-button-text"
					onClick={() => deleteOrDeprecate(false)}
					disabled={!allowDelete}
				/>
			</React.Fragment>
		);
	};

	const errorDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideErrorDialog} />
			</React.Fragment>
		);
	};

	const deleteOrDeprecateDialogs = (
		<>
			<Dialog
				visible={deleteDialog}
				className="w-30rem"
				header="Confirm Deletion"
				modal
				footer={deleteDialogFooter}
				onHide={hideDeleteDialog}
			>
				<div className="confirmation-content">
					<i className="pi pi-exclamation-triangle mr-3 text-4xl" />
					{
						<span>
							Warning: You are about to delete this data object from the database. This cannot be undone. Please confirm
							deletion or cancel.
						</span>
					}
				</div>
			</Dialog>

			<Dialog
				visible={deprecateDialog}
				className="w-30rem"
				header="Confirm Deletion"
				modal
				footer={deprecateDialogFooter}
				onHide={hideDeprecateDialog}
			>
				<div className="confirmation-content">
					<p>
						<i className="pi pi-exclamation-triangle mr-3 text-4xl" />
						Warning: You are about to delete this data object from the database. This cannot be undone. Please confirm
						the following information or deprecate instead:
					</p>
					<br />
				</div>
				<div>
					<Checkbox onChange={(e) => setAllowDelete(!allowDelete)} checked={allowDelete}></Checkbox>
					<label>
						{' '}
						This data object has not been made public OR this data object has been made public but fits criteria for
						deletion from the database
					</label>
				</div>
			</Dialog>

			<Dialog
				visible={errorDialog}
				className="w-30rem"
				header="Deletion Error"
				modal
				footer={errorDialogFooter}
				onHide={hideErrorDialog}
			>
				<div className="error-message-dialog">
					<i className="pi pi-ban mr-3 text-4xl" />
					{
						<span>
							ERROR: The data object you are trying to delete is in use by other data objects. Remove data connections
							to all other data objects and try to delete again.
						</span>
					}
				</div>
				<hr />
				<div className="error-message-detail">{<span className="text-sm">{deletionErrorMessage}</span>}</div>
			</Dialog>
		</>
	);

	return { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs };
};
