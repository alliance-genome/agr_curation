import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { CrossReferenceTemplate } from '../../../components/Templates/reference/CrossReferenceTemplate';
import { ShortCitationTemplate } from '../../../components/Templates/reference/ShortCitationTemplate';
import { FieldErrors } from '../../../components/Editors/fields/FieldErrors';

/**
 * A detail or create page's references table: the entity's references shown, not edited, each with a delete
 * button, and each row's error under its curie. Paged and filterable.
 *
 * @param {object[]} references - the references, each carrying a `dataKey`
 * @param {object} editingRows - rows in edit mode, keyed by dataKey
 * @param {object} errorMessages - row errors, keyed by `dataKey` then field
 * @param {Function} onRowEditChange - PrimeReact row-edit handler
 * @param {object} tableRef - ref forwarded to the DataTable
 * @param {(event: object, dataKey: string) => void} deletionHandler - removes a reference
 * @returns {JSX.Element}
 */
export const ReferencesFormTable = ({
	references,
	editingRows,
	errorMessages,
	onRowEditChange,
	tableRef,
	deletionHandler,
}) => {
	return (
		<DataTable
			value={references}
			dataKey="dataKey"
			showGridlines
			editMode="row"
			removableSort
			filterDisplay="row"
			size="small"
			editingRows={editingRows}
			resizableColumns
			columnResizeMode="fit"
			onRowEditChange={onRowEditChange}
			ref={tableRef}
			cellMemo={false}
			paginator
			paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
			currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
			rows={5}
			rowsPerPageOptions={[5, 10, 20, 50]}
		>
			<Column
				body={(data) => <DeleteAction deletionHandler={deletionHandler} id={data?.dataKey} />}
				header="Action"
				className="min-w-4rem max-w-4rem"
				bodyClassName="text-center"
				frozen
			/>
			<Column
				field="curie"
				header="Curie"
				body={(data) => (
					<>
						{data.curie}
						<FieldErrors messages={[errorMessages?.[data.dataKey]?.curie]} layout="inline" />
					</>
				)}
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="crossReferences"
				header="Cross References"
				body={(data) => <CrossReferenceTemplate reference={data} />}
				filter
				filterField="crossReferencesFilter"
				filterMatchMode="contains"
				showFilterMenu={false}
			/>
			<Column
				field="shortCitation"
				sortable
				header="Short Citation"
				body={(data) => <ShortCitationTemplate reference={data} />}
				filterField="shortCitation"
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
		</DataTable>
	);
};
