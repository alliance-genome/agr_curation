import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export const SecondaryIdsFormTable = ({ secondaryIds, tableRef }) => {
	return (
		<DataTable
			value={secondaryIds}
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
			emptyMessage="No secondary IDs available"
		>
			<Column
				field="identifier"
				header="Secondary Identifier"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
		</DataTable>
	);
};
