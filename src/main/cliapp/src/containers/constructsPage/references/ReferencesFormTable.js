import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { CrossReferenceTemplate } from '../../../components/Templates/reference/CrossReferenceTemplate';
import { ShortCitationTemplate } from '../../../components/Templates/reference/ShortCitationTemplate';

export const ReferencesFormTable = ({ references, tableRef }) => {
	return (
		<DataTable
			value={references}
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
			emptyMessage="No references available"
		>
			<Column
				field="curie"
				header="Curie"
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
				header="Short Citation"
				sortable
				body={(data) => <ShortCitationTemplate reference={data} />}
				filterField="shortCitation"
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
		</DataTable>
	);
};
