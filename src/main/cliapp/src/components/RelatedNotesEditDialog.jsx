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
import { Endpoints } from '../constants/Endpoints';
import { useVocabularyTermSetService } from '../service/useVocabularyTermSetService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../utils/rows';
import { TableEditorProvider } from './Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from './Editors/fields/strategies/useLocalRowsStrategy';
import { ControlledVocabularyTableEditor } from './Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { InputTextAreaTableEditor } from './Editors/text/InputTextAreaTableEditor';
import { ReferencesTableEditor } from './Editors/autocomplete/references/ReferencesTableEditor';
import { BooleanTableEditor } from './Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing a main-table row's related notes. Edits stay local until Keep Edits validates every note
 * and hands the list back through the row's editor callback.
 *
 * @param {object} originalRelatedNotesData - the row's notes, dialog visibility, row index and PrimeReact editor
 *   options
 * @param {Function} setOriginalRelatedNotesData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @param {string} noteTypeVocabularyTermSet - the term set the note types come from
 * @param {boolean} [showReferences=true] - whether to offer the references column
 * @returns {JSX.Element}
 */
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

	const noteTypeTerms = useVocabularyTermSetService(noteTypeVocabularyTermSet);

	const strategy = useLocalRowsStrategy({
		rows: localRelatedNotes,
		setRows: setLocalRelatedNotes,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
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
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localRelatedNotes);
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
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localRelatedNotes}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						headerColumnGroup={headerGroup}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						ref={tableRef}
						cellMemo={false}
					>
						<Column
							editor={(editorOptions) => (
								<DeleteAction deletionHandler={handleDeleteRelatedNote} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="noteType"
									options={noteTypeTerms}
									showClear={false}
								/>
							)}
							field="noteType"
							header="Note Type"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<InputTextAreaTableEditor editorOptions={editorOptions} field="freeText" rows={5} cols={30} />
							)}
							field="freeText"
							header="Text"
							headerClassName="surface-0"
							className="wrap-word max-w-35rem"
						/>
						{showReferences && (
							<Column
								editor={(editorOptions) => <ReferencesTableEditor editorOptions={editorOptions} />}
								field="evidence"
								header="Evidence"
								headerClassName="surface-0"
								className="wrap-word max-w-25rem"
							/>
						)}
						<Column
							editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
							field="internal"
							header="Internal"
							headerClassName="surface-0"
						/>
					</DataTable>
				</TableEditorProvider>
			</Dialog>
		</div>
	);
};
