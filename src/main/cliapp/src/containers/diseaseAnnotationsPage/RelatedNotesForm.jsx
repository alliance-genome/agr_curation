import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { TableEditorProvider } from '../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { InputTextAreaTableEditor } from '../../components/Editors/text/InputTextAreaTableEditor';
import { KEEP_ROWS_IN_EDIT_MODE } from '../allelesPage/utils';

/**
 * The new disease annotation dialog's related notes table, every row always in edit mode. Rows live
 * in the new annotation reducer and are addressed by their `dataKey`.
 *
 * @param {Function} dispatch - new annotation reducer dispatch
 * @param {object[]} relatedNotes - the annotation's notes, each carrying a `dataKey`
 * @param {boolean} showRelatedNotes - whether the table is shown
 * @param {object} errorMessages - row errors, keyed by `dataKey` then field
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @returns {JSX.Element}
 */
export const RelatedNotesForm = ({ dispatch, relatedNotes, showRelatedNotes, errorMessages, editingRows }) => {
	const noteTypeTerms = useVocabularyTermSetService('da_note_type');

	const strategy = useDataKeyRowsStrategy({
		rows: relatedNotes,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW', tableType: 'relatedNotes', dataKey: rowKey, field, value }),
	});

	const createNewNoteHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_NEW_NOTE' });
	};

	const handleDeleteRelatedNote = (event, editorOptions) => {
		event.preventDefault();
		dispatch({
			type: 'DELETE_ROW',
			tableType: 'relatedNotes',
			showType: 'showRelatedNotes',
			dataKey: editorOptions.rowData?.dataKey,
		});
	};

	const deleteAction = (editorOptions) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text"
				onClick={(event) => {
					handleDeleteRelatedNote(event, editorOptions);
				}}
			/>
		);
	};

	return (
		<div>
			{showRelatedNotes && (
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={relatedNotes}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						editingRows={editingRows}
						cellMemo={false}
					>
						<Column
							editor={(editorOptions) => deleteAction(editorOptions)}
							body={(rowData) => deleteAction({ rowData })}
							style={{ maxWidth: '4rem' }}
							frozen
							headerClassName="surface-0"
							bodyStyle={{ textAlign: 'center' }}
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="noteType"
									options={noteTypeTerms}
									dataKey="id"
									showClear={false}
								/>
							)}
							field="noteType.name"
							header="Note Type"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
							field="internal"
							header="Internal"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<InputTextAreaTableEditor editorOptions={editorOptions} field="freeText" rows={5} cols={30} />
							)}
							field="freeText"
							header="Text"
							headerClassName="surface-0"
						/>
					</DataTable>
				</TableEditorProvider>
			)}
			<div className={`w-2 ${showRelatedNotes ? 'pt-3' : ''} p-field p-col`}>
				<Button label="Add Note" onClick={createNewNoteHandler} />
			</div>
		</div>
	);
};
