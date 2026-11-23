import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Endpoints } from '../constants/Endpoints';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from './Actions/DeletionAction';
import { ValidationService } from '../service/ValidationService';
import { useVocabularyTermSetService } from '../service/useVocabularyTermSetService';
import { validate } from '../utils/utils';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils/rows';
import { TableEditorProvider } from './Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from './Editors/fields/strategies/useLocalRowsStrategy';
import { ControlledVocabularyTableEditor } from './Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { InputTextAreaTableEditor } from './Editors/text/InputTextAreaTableEditor';
import { BooleanTableEditor } from './Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing the note held by a row of an editable table, such as an allele's gene association. Edits
 * stay local until Keep Edits validates every note and hands them to `onChange`; the row's note cell is then
 * marked as having pending edits.
 *
 * @param {object} relatedNotesData - the row's notes, dialog visibility, the row's dataKey and PrimeReact editor
 *   options
 * @param {Function} setRelatedNotesData - its setter
 * @param {object} tableErrorMessages - the table's errors, keyed by dataKey
 * @param {Function} setTableErrorMessages - their setter
 * @param {boolean} [singleValue=false] - whether the row holds at most one note
 * @param {(notes: object[], editorOptions: object) => void} onChange - stores the kept notes on the row
 * @param {{noteType: string}} defaultValues - the name of the note type a new note starts with
 * @param {string} [errorField='relatedNotes'] - the row field the notes are held under
 * @param {string} noteTypeVocabType - the term set the note types come from
 * @returns {JSX.Element}
 */
export const RelatedNotesDialogEditOnly = ({
	relatedNotesData,
	tableErrorMessages,
	setTableErrorMessages,
	setRelatedNotesData,
	singleValue = false,
	onChange,
	defaultValues,
	errorField = 'relatedNotes',
	noteTypeVocabType,
}) => {
	const { originalRelatedNotes, dialogIsVisible, dataKey } = relatedNotesData;
	const [errorMessages, setErrorMessages] = useState({});
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toastRef = useRef(null);
	const [editingRows, setEditingRows] = useState({});
	const [localRelatedNotes, setLocalRelatedNotes] = useState([]);
	const noteTypeTerms = useVocabularyTermSetService(noteTypeVocabType);

	const strategy = useLocalRowsStrategy({
		rows: localRelatedNotes,
		setRows: setLocalRelatedNotes,
		errors: errorMessages,
	});

	const cloneNotes = (clonableNotes) => {
		if (!clonableNotes) {
			return [
				{
					dataKey: 0,
					noteType: {
						name: defaultValues['noteType'] || ' ',
					},
					internal: false,
				},
			];
		}
		let _clonableNotes = structuredClone(clonableNotes);
		_clonableNotes.forEach((note, index) => {
			note.dataKey = index;
		});
		return _clonableNotes;
	};

	const showDialogHandler = () => {
		let _localRelatedNotes = cloneNotes(originalRelatedNotes);
		setLocalRelatedNotes(_localRelatedNotes);

		let rowsObject = {};
		_localRelatedNotes.forEach((note) => {
			rowsObject[`${note.dataKey}`] = true;
		});
		setEditingRows(rowsObject);
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setLocalRelatedNotes((previous) => previous.filter((relatedNote) => relatedNote.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const remaining = { ...previous };
			delete remaining[dataKey];
			return remaining;
		});
	};

	const validateTable = async () => {
		const results = await validate(localRelatedNotes, Endpoints.Entity.NOTE, validationService);
		const errors = {};
		results.forEach((result, index) => {
			if (!result.isError) return;
			const rowErrors = {};
			Object.entries(result.data ?? {}).forEach(([field, message]) => {
				rowErrors[field] = { severity: 'error', message };
			});
			errors[localRelatedNotes[index].dataKey] = rowErrors;
		});
		setErrorMessages(errors);
		const anyErrors = Object.keys(errors).length > 0;
		if (anyErrors) {
			toastRef.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Validation error',
				detail: 'Please fix validation errors before saving',
			});
		}
		return anyErrors;
	};

	const saveDataHandler = async () => {
		setErrorMessages({});
		const anyErrors = await validateTable();
		if (anyErrors) return;
		onChange(localRelatedNotes, relatedNotesData.rowProps);

		const tableErrorMessagesCopy = structuredClone(tableErrorMessages);
		const errorMessage = {
			...tableErrorMessagesCopy[dataKey],
			[errorField]: {
				severity: 'warn',
				message: 'Pending Edits!',
			},
		};
		tableErrorMessagesCopy[dataKey] = errorMessage;
		setTableErrorMessages(tableErrorMessagesCopy);
		setRelatedNotesData((relatedNotesData) => {
			return {
				...relatedNotesData,
				dialogIsVisible: false,
			};
		});
		setLocalRelatedNotes([]);
	};

	const createNewNoteHandler = () => {
		const _localRelatedNotes = structuredClone(localRelatedNotes);
		const newNote = {
			noteType: {
				name: defaultValues['noteType'] || ' ',
			},
			internal: false,
		};

		addDataKey(newNote);

		_localRelatedNotes.push(newNote);

		let _editingRows = { ...editingRows, ...{ [`${newNote.dataKey}`]: true } };
		setLocalRelatedNotes(_localRelatedNotes);
		setEditingRows(_editingRows);
	};

	const hideDialog = () => {
		setErrorMessages({});
		setRelatedNotesData((relatedNotesData) => {
			return {
				...relatedNotesData,
				dialogIsVisible: false,
			};
		});
		let _localRelatedNotes = [];
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const footerTemplate = () => {
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button
					label="New Note"
					icon="pi pi-plus"
					onClick={createNewNoteHandler}
					disabled={singleValue && localRelatedNotes.length > 0}
				/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} />
			</div>
		);
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Note Type" />
				<Column header="Text" />
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toastRef} position="top-right" />
			<Dialog
				visible={dialogIsVisible}
				className="w-8"
				modal
				onHide={hideDialog}
				closable
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
						size="small"
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						ref={tableRef}
						cellMemo={false}
					>
						<Column
							editor={(editorOptions) => (
								<DeleteAction deletionHandler={deletionHandler} id={editorOptions?.rowData?.dataKey} />
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
							field="noteType.name"
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
