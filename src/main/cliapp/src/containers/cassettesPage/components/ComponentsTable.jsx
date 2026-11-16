import { useMemo, useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { RelatedNotesEditDialog } from '../../../components/RelatedNotesEditDialog';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';

const NO_MAIN_ROW_ERRORS = {};

/**
 * The editable table of one of a cassette's component lists, every row always in edit mode: the rows of the Cassettes
 * table's component pop-up (SCRUM-6752) and of a section of the cassette detail page (SCRUM-6747). Its owner holds the
 * rows and saves them; the table edits and deletes them, and edits each row's related notes in the shared notes pop-up.
 *
 * @param {object[]} rows - the rows, each carrying a `dataKey`
 * @param {Function} setRows - the rows' setter
 * @param {object} errorMessages - the rows' messages, by `dataKey` then field
 * @param {Function} setErrorMessages - the messages' setter
 * @param {boolean} [isLoading] - whether the rows are still being read
 * @param {object} config - the list, from componentConfigs
 * @returns {JSX.Element}
 */
export const ComponentsTable = ({ rows, setRows, errorMessages, setErrorMessages, isLoading = false, config }) => {
	const [relatedNotesData, setRelatedNotesData] = useState({ dialog: false });
	const tableRef = useRef(null);
	const relationTerms = useVocabularyTermSetService(config.relationTermSet);

	const editingRows = useMemo(() => Object.fromEntries(rows.map((row) => [row.dataKey, true])), [rows]);

	const writeRow = (rowKey, field, value) =>
		setRows((previous) => previous.map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row)));

	const strategy = useDataKeyRowsStrategy({
		rows,
		errors: errorMessages,
		write: ({ rowKey, field, value }) => writeRow(rowKey, field, value),
	});

	const deleteRow = (event, dataKey) => {
		event.preventDefault();
		setRows((previous) => previous.filter((row) => row.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const updated = { ...previous };
			delete updated[dataKey];
			return updated;
		});
	};

	// The notes of one row are edited in the shared notes pop-up, which hands them back through the row's callback
	const openRelatedNotes = (event, editorOptions) => {
		event.preventDefault();
		const rowKey = editorOptions.rowData?.dataKey;
		const row = rows.find((candidate) => candidate.dataKey === rowKey);
		setRelatedNotesData({
			originalRelatedNotes: row?.relatedNotes,
			dialog: true,
			isInEdit: true,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: { ...editorOptions, editorCallback: (notes) => writeRow(rowKey, 'relatedNotes', notes) },
		});
	};

	return (
		<>
			<TableEditorProvider strategy={strategy}>
				<DataTable
					value={rows}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					size="small"
					editingRows={editingRows}
					onRowEditChange={() => null}
					resizableColumns
					columnResizeMode="expand"
					cellMemo={false}
					loading={isLoading}
					ref={tableRef}
				>
					<Column
						editor={(editorOptions) => (
							<DeleteAction deletionHandler={deleteRow} id={editorOptions?.rowData?.dataKey} />
						)}
						header="Actions"
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					{config.columns({ openRelatedNotes, relationTerms }).map(({ field, header, editor }) => (
						<Column key={field} field={field} header={header} headerClassName="surface-0" editor={editor} />
					))}
					<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
					<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
				</DataTable>
			</TableEditorProvider>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={NO_MAIN_ROW_ERRORS}
				setErrorMessagesMainRow={() => null}
				noteTypeVocabularyTermSet={config.noteTypeVocabularyTermSet}
			/>
		</>
	);
};
