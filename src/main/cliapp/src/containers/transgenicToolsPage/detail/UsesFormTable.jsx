import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { UsesTableEditor } from '../../../components/Editors/autocomplete/ontology/UsesTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';

/**
 * Editable transgenic tool uses table, every row always in edit mode. Each row holds one or more FBcv terms.
 * Its editors read and write rows through the table strategy its parent provides.
 *
 * @param {object[]} uses - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @returns {JSX.Element}
 */
export const UsesFormTable = ({ uses, editingRows, onRowEditChange, tableRef, deletionHandler }) => (
	<DataTable
		value={uses}
		dataKey="dataKey"
		showGridlines
		editMode="row"
		size="small"
		editingRows={editingRows}
		resizableColumns
		columnResizeMode="expand"
		cellMemo={false}
		onRowEditChange={onRowEditChange}
		ref={tableRef}
	>
		<Column
			editor={(editorOptions) => (
				<DeleteAction deletionHandler={deletionHandler} id={editorOptions?.rowData?.dataKey} />
			)}
			header="Actions"
			className="max-w-4rem"
			bodyClassName="text-center"
			headerClassName="surface-0"
			frozen
		/>
		<Column
			editor={(editorOptions) => <UsesTableEditor editorOptions={editorOptions} />}
			field="uses"
			header="Uses"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
			field="internal"
			header="Internal"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="obsolete" />}
			field="obsolete"
			header="Obsolete"
			headerClassName="surface-0"
		/>
		<Column
			editor={(editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />}
			field="evidence"
			header="Evidence"
			headerClassName="surface-0"
		/>
		<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
		<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
	</DataTable>
);
