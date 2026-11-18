import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../Actions/DeletionAction';
import { ControlledVocabularyTableEditor } from '../Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { InputTextAreaTableEditor } from '../Editors/text/InputTextAreaTableEditor';
import { BooleanTableEditor } from '../Editors/dropdown/boolean/BooleanTableEditor';
import { ReferencesTableEditor } from '../Editors/autocomplete/references/ReferencesTableEditor';

/**
 * A detail or create page's related notes table, every row always in edit mode, each with a delete button. Its editors
 * read and write rows through the table strategy supplied by the surrounding TableEditorProvider.
 *
 * @param {object[]} relatedNotes - the notes, each carrying a `dataKey`
 * @param {object} editingRows - rows in edit mode, keyed by dataKey
 * @param {Function} onRowEditChange - keeps the rows in edit mode
 * @param {object} tableRef - ref forwarded to the DataTable
 * @param {(event: object, dataKey: string) => void} deletionHandler - removes a note
 * @param {object[]} [noteTypeTerms] - the note types the notes can take
 * @returns {JSX.Element}
 */
export const RelatedNotesFormTable = ({
	relatedNotes,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	noteTypeTerms,
}) => {
	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Note Type" />
				<Column header="Text" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={relatedNotes}
			dataKey="dataKey"
			showGridlines
			editMode="row"
			headerColumnGroup={headerGroup}
			size="small"
			editingRows={editingRows}
			resizableColumns
			columnResizeMode="expand"
			cellMemo={false}
			onRowEditChange={onRowEditChange}
			ref={tableRef}
		>
			<Column
				editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props?.rowData?.dataKey} />}
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
				className="w-4"
			/>
			<Column
				editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
				field="internal"
				header="Internal"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <ReferencesTableEditor editorOptions={editorOptions} />}
				field="references.curie"
				header="Evidence"
				headerClassName="surface-0"
			/>
		</DataTable>
	);
};
