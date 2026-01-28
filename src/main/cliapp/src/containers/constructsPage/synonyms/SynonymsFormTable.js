import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';

export const SynonymsFormTable = ({ synonyms, tableRef }) => {
	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

	return (
		<DataTable
			value={synonyms}
			dataKey="id"
			showGridlines
			removableSort
			filterDisplay="row"
			size="small"
			resizableColumns
			columnResizeMode="fit"
			ref={tableRef}
			paginator
			paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
			currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
			rows={5}
			rowsPerPageOptions={[5, 10, 20, 50]}
			emptyMessage="No synonyms available"
		>
			<Column
				field="displayText"
				header="Display Text"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="formatText"
				header="Format Text"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="nameType.name"
				header="Name Type"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="internal"
				header="Internal"
				body={internalTemplate}
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
		</DataTable>
	);
};
