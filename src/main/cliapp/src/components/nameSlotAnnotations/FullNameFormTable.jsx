import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../Actions/DeletionAction';
import { InputTextTableEditor } from '../Editors/text/InputTextTableEditor';
import { ControlledVocabularyTableEditor } from '../Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../Editors/autocomplete/references/EvidenceTableEditor';

/**
 * A detail page's full or systematic name table: the entity's one name, always in edit mode, with a delete
 * button. Its editors read and write the row through the table strategy supplied by the surrounding
 * TableEditorProvider.
 *
 * @param {object[]} name - the name as the table's only row
 * @param {object} editingRows - rows in edit mode, keyed by dataKey
 * @param {Function} onRowEditChange - keeps the row in edit mode
 * @param {object} tableRef
 * @param {Function} deletionHandler - removes the name
 * @param {object[]} [synonymScopeTerms] - the synonym scope vocabulary
 * @param {object[]} [nameTypeTerms] - the name types the name can take
 * @returns {JSX.Element}
 */
export const FullNameFormTable = ({
	name,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	synonymScopeTerms,
	nameTypeTerms,
}) => {
	let headerGroup = (
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
			value={name}
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
				editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
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
						showClear={true}
					/>
				)}
				field="synonymScope.name"
				header="Synonym Scope"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="nameType"
						options={nameTypeTerms}
						showClear={false}
					/>
				)}
				field="nameType.name"
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
				field="evidence.curie"
				header="Evidence"
				headerClassName="surface-0"
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
		</DataTable>
	);
};
