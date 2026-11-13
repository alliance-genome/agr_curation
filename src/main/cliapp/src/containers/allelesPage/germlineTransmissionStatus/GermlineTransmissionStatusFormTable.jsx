import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';

/**
 * The allele detail and create pages' Germline Transmission Status table: the allele's one germline transmission
 * status, always in edit mode. Its editors read and write the row through the table strategy supplied by the
 * surrounding TableEditorProvider.
 *
 * @param {object[]} germlineTransmissionStatuses - the status as the table's only row
 * @param {object} editingRows - rows in edit mode, keyed by dataKey
 * @param {Function} onRowEditChange - keeps the row in edit mode
 * @param {object} tableRef
 * @param {Function} deletionHandler - removes the status
 * @param {object[]} [germlineTransmissionStatusTerms] - the germline transmission status vocabulary
 * @returns {JSX.Element}
 */
export const GermlineTransmissionStatusFormTable = ({
	germlineTransmissionStatuses,
	editingRows,
	onRowEditChange,
	tableRef,
	deletionHandler,
	germlineTransmissionStatusTerms,
}) => {
	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Germline Transmission Status" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={germlineTransmissionStatuses}
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
				editor={(editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="germlineTransmissionStatus"
						options={germlineTransmissionStatusTerms}
						showClear={false}
					/>
				)}
				field="germlineTransmissionStatus"
				header="Germline Transmission Status"
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
		</DataTable>
	);
};
