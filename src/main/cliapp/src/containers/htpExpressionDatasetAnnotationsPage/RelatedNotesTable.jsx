import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { getRefStrings } from '../../utils/utils';

/**
 * Read-only, always-expanded table for a dataset's related notes - same columns as
 * RelatedNotesReadOnlyDialog, but shown inline on the detail page rather than behind a pop-up.
 */
export const RelatedNotesTable = ({ relatedNotes }) => {
	const noteTypeTemplate = (rowData) => <EllipsisTableCell>{rowData.noteType?.name}</EllipsisTableCell>;
	const textTemplate = (rowData) => <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
	const evidenceTemplate = (rowData) => <StringListTemplate list={getRefStrings(rowData?.references)} />;
	const internalTemplate = (rowData) => <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;

	return (
		<DataTable value={relatedNotes || []} dataKey="id" showGridlines emptyMessage="No notes available">
			<Column field="noteType.name" header="Note Type" body={noteTypeTemplate} />
			<Column field="freeText" header="Text" body={textTemplate} className="wrap-word max-w-35rem" />
			<Column field="evidence.curie" header="Evidence" body={evidenceTemplate} className="wrap-word max-w-25rem" />
			<Column field="internal" header="Internal" body={internalTemplate} />
		</DataTable>
	);
};
