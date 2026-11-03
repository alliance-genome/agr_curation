import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { FunctionalImpactsTableEditor } from '../../../components/Editors/autocomplete/vocabularyTerm/FunctionalImpactsTableEditor';
import { PhenotypeTermTableEditor } from '../../../components/Editors/autocomplete/ontology/PhenotypeTermTableEditor';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Editable functional impacts table, every row always in edit mode. Its editors read and write
 * rows through the table strategy its parent provides.
 *
 * @param {object[]} functionalImpacts - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @returns {JSX.Element}
 */
export const FunctionalImpactsFormTable = ({
	functionalImpacts,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
}) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Functional Impacts" />
				<Column header="Phenotype Term" />
				<Column header="Phenotype Statement" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={functionalImpacts}
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
				editor={(editorOptions) => <FunctionalImpactsTableEditor editorOptions={editorOptions} />}
				field="functionalImpacts"
				header="Functional Impacts"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <PhenotypeTermTableEditor editorOptions={editorOptions} />}
				field="phenotypeTerm"
				header="Phenotype Term"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="phenotypeStatement" />}
				field="phenotypeStatement"
				header="Phenotype Statement"
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
