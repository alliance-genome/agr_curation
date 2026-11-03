import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { PhenotypeTermTableEditor } from '../../../components/Editors/autocomplete/ontology/PhenotypeTermTableEditor';
import { InputTextAreaTableEditor } from '../../../components/Editors/text/InputTextAreaTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Editable inheritance modes table, every row always in edit mode. Its editors read and write rows
 * through the table strategy its parent provides.
 *
 * @param {object[]} inheritanceModes - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @param {object[]} [inheritanceModeTerms] - the allele_inheritance_mode vocabulary terms,
 *   undefined until they load
 * @returns {JSX.Element}
 */
export const InheritanceModesFormTable = ({
	inheritanceModes,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	inheritanceModeTerms,
}) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Inheritance Mode" />
				<Column header="Phenotype Term" />
				<Column header="Phenotype Statement" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={inheritanceModes}
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
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="inheritanceMode"
						options={inheritanceModeTerms}
						dataKey="id"
						showClear={false}
					/>
				)}
				field="inheritanceMode"
				header="Inheritance Mode"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <PhenotypeTermTableEditor editorOptions={editorOptions} />}
				field="phenotypeTerm"
				header="Phenotype Term"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => (
					<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" rows={1} cols={30} />
				)}
				field="phenotypeStatement"
				header="Phenotype Statement"
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
