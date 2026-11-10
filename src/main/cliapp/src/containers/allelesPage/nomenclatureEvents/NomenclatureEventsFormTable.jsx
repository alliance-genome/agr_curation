import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Editable nomenclature events table, every row always in edit mode. Its editors read and write rows
 * through the table strategy its parent provides.
 *
 * @param {object[]} nomenclatureEvents - rows, each carrying a `dataKey`
 * @param {object} editingRows - PrimeReact editing state, keyed by `dataKey`
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref to the DataTable
 * @param {Function} deletionHandler - `(event, dataKey)`
 * @param {object[]} [nomenclatureEventTerms] - the allele_nomenclature_event vocabulary terms,
 *   undefined until they load
 * @returns {JSX.Element}
 */
export const NomenclatureEventsFormTable = ({
	nomenclatureEvents,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	nomenclatureEventTerms,
}) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Nomenclature Event" />
				<Column header="Evidence" />
				<Column header="Internal" />
				<Column header="Obsolete" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
				<Column header="Created By" />
				<Column header="Date Created" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={nomenclatureEvents}
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
						field="nomenclatureEvent"
						options={nomenclatureEventTerms}
						dataKey="id"
						showClear={false}
					/>
				)}
				field="nomenclatureEvent"
				header="Nomenclature Event"
				headerClassName="surface-0"
			/>
			<Column
				editor={(editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />}
				field="evidence"
				header="Evidence"
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
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
			<Column field="createdBy.uniqueId" header="Created By" />
			<Column field="dateCreated" header="Date Created" />
		</DataTable>
	);
};
