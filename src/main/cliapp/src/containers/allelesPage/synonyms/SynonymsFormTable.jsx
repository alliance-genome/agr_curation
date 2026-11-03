import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Editable synonyms table, every row always in edit mode. Its editors read and write rows
 * through the table strategy its parent provides.
 *
 * @param {object[]} synonyms - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @param {object[]} [synonymScopeTerms] - the synonym_scope vocabulary terms, undefined until
 *   they load
 * @param {object[]} [nameTypeTerms] - the name_type vocabulary terms, undefined until they load
 * @returns {JSX.Element}
 */
export const SynonymsFormTable = ({
	synonyms,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	synonymScopeTerms,
	nameTypeTerms,
}) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Display Text" />
				<Column header="Format Text" />
				<Column header="Synonym Scope" />
				<Column header="Name Type" />
				<Column header="Synonym URL" />
				<Column header="Internal" />
				<Column header="Evidence" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={synonyms}
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
				editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="displayText" />}
				field="displayText"
				header="Display Text"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="formatText" />}
				field="formatText"
				header="Format Text"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="synonymScope"
						options={synonymScopeTerms}
						dataKey="id"
						showClear={true}
					/>
				)}
				field="synonymScope"
				header="Synonym Scope"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="nameType"
						options={nameTypeTerms}
						dataKey="id"
						showClear={false}
					/>
				)}
				field="nameType"
				header="Name Type"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="synonymUrl" />}
				field="synonymUrl"
				header="Synonym URL"
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
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
		</DataTable>
	);
};
