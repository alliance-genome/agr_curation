import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import {
	synonymScopeTemplate,
	nameTypeTemplate,
	synonymUrlTemplate,
	displayTextTemplate,
	formatTextTemplate,
} from '../../../components/NameSlotAnnotationComponent';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

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
				body={displayTextTemplate}
			/>
			<Column
				field="formatText"
				header="Format Text"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={formatTextTemplate}
			/>
			<Column field="synonymScope" header="Synonym Scope" body={synonymScopeTemplate} />
			<Column
				field="nameType.name"
				header="Name Type"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={nameTypeTemplate}
			/>
			<Column
				field="synonymUrl"
				header="Synonym URL"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={synonymUrlTemplate}
			/>
			<Column
				field="internal"
				header="Internal"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={internalTemplate}
			/>
			<Column field="evidence.curie" header="Evidence" body={evidenceTemplate} />
			<Column field="updatedBy.uniqueId" header="Updated By" sortable />
			<Column field="dateUpdated" header="Date Updated" sortable />
		</DataTable>
	);
};
