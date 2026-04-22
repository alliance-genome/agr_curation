import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../service/ValidationService';
import { DeleteAction } from './Actions/DeletionAction';
import { InternalEditor } from './Editors/InternalEditor';
import { ReferencesEditor } from './Editors/ReferencesEditor';
import { VocabularyTermSetEditor } from './Editors/VocabularyTermSetEditor';
import { TableInputTextAreaEditor } from './Editors/TableInputTextAreaEditor';
import { Endpoints } from '../constants/Endpoints';
import { multipleAutocompleteOnChange } from '../utils/utils';

export const RelatedNotesEditDialog = ({
	originalRelatedNotesData,
	setOriginalRelatedNotesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
	noteTypeVocabularyTermSet,
	showReferences = true,
}) => {
	const { originalRelatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = originalRelatedNotesData;
	const [localRelatedNotes, setLocalRelatedNotes] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		dataKeyCounter.current = 0;
		let _localRelatedNotes = cloneNotes(originalRelatedNotes);
		setLocalRelatedNotes(_localRelatedNotes);

		let rowsObject = {};
		if (_localRelatedNotes) {
			_localRelatedNotes.forEach((note) => {
				rowsObject[`${note.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const onRowEditChange = () => {
		return null;
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalRelatedNotesData((originalRelatedNotesData) => {
			return {
				...originalRelatedNotesData,
				dialog: false,
			};
		});
		setLocalRelatedNotes([]);
	};

	const cloneNotes = (clonableNotes) => {
		let _clonableNotes = structuredClone(clonableNotes);
		if (_clonableNotes) {
			_clonableNotes.forEach((note) => {
				note.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableNotes = [];
		}
		return _clonableNotes;
	};

	const noteTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalRelatedNotes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], noteType: event.value };
			return updated;
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalRelatedNotes((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const referencesOnChangeHandler = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'references', setFieldValue);
		setLocalRelatedNotes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], references: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalRelatedNotes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const createNewNoteHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localRelatedNotes = structuredClone(localRelatedNotes);
		_localRelatedNotes.push({
			dataKey: newKey,
			noteType: {
				name: '',
			},
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const handleDeleteRelatedNote = (e, dataKey) => {
		let _localRelatedNotes = structuredClone(localRelatedNotes);
		_localRelatedNotes = _localRelatedNotes.filter((note) => note.dataKey !== dataKey);
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localRelatedNotes = structuredClone(localRelatedNotes);
		for (const note of _localRelatedNotes) {
			delete note.dataKey;
		}
		mainRowProps.rowData.relatedNotes = _localRelatedNotes;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].relatedNotes = _localRelatedNotes;
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localRelatedNotes?.[0]?.freeText ?? null);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['relatedNotes'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalRelatedNotesData((originalRelatedNotesData) => {
			return {
				...originalRelatedNotesData,
				dialog: false,
			};
		});
	};

	const cleanForValidation = (obj) => {
		const _obj = structuredClone(obj);
		delete _obj.dataKey;
		delete _obj.updatedBy;
		delete _obj.createdBy;
		delete _obj.dateUpdated;
		delete _obj.dateCreated;
		if (_obj.references) {
			_obj.references.forEach((ref) => {
				delete ref.updatedBy;
				delete ref.createdBy;
				delete ref.dateUpdated;
				delete ref.dateCreated;
			});
		}
		return _obj;
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			let hasErrors = false;
			const newErrorMessages = {};

			for (let i = 0; i < localRelatedNotes.length; i++) {
				const _note = cleanForValidation(localRelatedNotes[i]);
				const result = await validationService.validate(Endpoints.Entity.NOTE, _note);
				const dataKey = localRelatedNotes[i].dataKey;
				if (result.isError) {
					hasErrors = true;
					newErrorMessages[dataKey] = {};
					if (result.data) {
						Object.keys(result.data).forEach((field) => {
							newErrorMessages[dataKey][field] = {
								severity: 'error',
								message: result.data[field],
							};
						});
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
				<Button label="New Note" icon="pi pi-plus" onClick={createNewNoteHandler} />
				<Button
					label="Keep Edits"
					icon={isValidating ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
					onClick={validateAndSave}
					disabled={isValidating}
				/>
			</div>
		);
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Note Type" />
				<Column header="Text" />
				{showReferences && <Column header="Evidence" />}
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

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
				<h3>Related Notes</h3>
				<DataTable
					value={localRelatedNotes}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					headerColumnGroup={headerGroup}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					ref={tableRef}
					cellMemo={false}
				>
					<Column
						editor={(props) => <DeleteAction deletionHandler={handleDeleteRelatedNote} id={props?.rowData?.dataKey} />}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<VocabularyTermSetEditor
									props={props}
									onChangeHandler={noteTypeOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									vocabType={noteTypeVocabularyTermSet}
									field="noteType"
									showClear={false}
								/>
							);
						}}
						field="noteType.name"
						header="Note Type"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<TableInputTextAreaEditor
									value={props.value}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									textOnChangeHandler={textOnChangeHandler}
									field="freeText"
									rows={5}
									columns={30}
								/>
							);
						}}
						field="freeText"
						header="Text"
						headerClassName="surface-0"
						className="wrap-word max-w-35rem"
					/>
					{showReferences && (
						<Column
							editor={(props) => {
								return (
									<ReferencesEditor
										props={props}
										errorMessages={errorMessages}
										onChange={referencesOnChangeHandler}
										dataKey={props?.rowData?.dataKey}
									/>
								);
							}}
							field="evidence.curie"
							header="Evidence"
							headerClassName="surface-0"
							className="wrap-word max-w-25rem"
						/>
					)}
					<Column
						editor={(props) => {
							return (
								<InternalEditor
									props={props}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									internalOnChangeHandler={internalOnChangeHandler}
								/>
							);
						}}
						field="internal"
						header="Internal"
						headerClassName="surface-0"
					/>
				</DataTable>
			</Dialog>
		</div>
	);
};
