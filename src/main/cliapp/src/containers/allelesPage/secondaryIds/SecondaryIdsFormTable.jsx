import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Editable secondary IDs table, every row always in edit mode. Its editors read and write rows
 * through the table strategy its parent provides.
 *
 * @param {object[]} secondaryIds - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @returns {JSX.Element}
 */
export const SecondaryIdsFormTable = ({ secondaryIds, editingRows, onRowEditChange, tableRef, deletionHandler }) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Secondary ID" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={secondaryIds}
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
				editor={(editorOptions) => (
					<DeleteAction deletionHandler={deletionHandler} id={editorOptions?.rowData?.dataKey} />
				)}
				className="max-w-4rem"
				bodyClassName="text-center"
				headerClassName="surface-0"
				frozen
			/>
			<Column
				editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="secondaryId" />}
				field="secondaryId"
				header="Secondary ID"
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
				editor={(editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />}
				field="evidence"
				header="Evidence"
				headerClassName="surface-0"
			/>
		</DataTable>
	);
};
