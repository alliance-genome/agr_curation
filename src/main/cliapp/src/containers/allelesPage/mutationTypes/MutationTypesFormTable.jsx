import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableField } from '../../../components/Editors/fields/TableField';
import { MultiAutocomplete } from '../../../components/Editors/widgets/MultiAutocomplete';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { mutationTypeLabel, mutationTypesSearch } from '../../../components/Editors/autocomplete/ontology/utils';

/**
 * Editable mutation types table, every row always in edit mode. Its editors read and write rows
 * through the table strategy its parent provides.
 *
 * @param {object[]} mutationTypes - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @returns {JSX.Element}
 */
export const MutationTypesFormTable = ({ mutationTypes, editingRows, onRowEditChange, tableRef, deletionHandler }) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Mutation Types" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={mutationTypes}
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
				editor={(editorOptions) => (
					<TableField editorOptions={editorOptions} field="mutationTypes">
						{(binding) => (
							<MultiAutocomplete {...binding} search={mutationTypesSearch} selectedItemTemplate={mutationTypeLabel} />
						)}
					</TableField>
				)}
				field="mutationTypes"
				header="Mutation Types"
				headerClassName="surface-0"
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
