import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export const ConstructComponentsFormTable = ({ components, tableRef }) => {
	const relationTemplate = (rowData) => {
		if (!rowData?.relation?.name) return null;
		let relationName = rowData.relation.name;
		// Remove RO: suffix if present
		if (relationName.indexOf(' (RO:') !== -1) {
			relationName = relationName.substring(0, relationName.indexOf(' (RO:'));
		}
		return relationName;
	};

	const taxonTemplate = (rowData) => {
		if (!rowData?.taxon) return null;
		const name = rowData.taxon.name || '';
		const curie = rowData.taxon.curie || '';
		if (name && curie) {
			return `${name} (${curie})`;
		}
		return name || curie;
	};

	const relatedNotesTemplate = (rowData) => {
		if (!rowData?.relatedNotes || rowData.relatedNotes.length === 0) return null;
		return rowData.relatedNotes.map((note, index) => (
			<div key={note.id || index}>{note.freeText}</div>
		));
	};

	const evidenceTemplate = (rowData) => {
		if (!rowData?.evidence || rowData.evidence.length === 0) return null;
		return rowData.evidence.map((ref, index) => (
			<div key={ref.id || index}>{ref.curie || ref.shortCitation}</div>
		));
	};

	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

	return (
		<DataTable
			value={components}
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
			emptyMessage="No construct components available"
		>
			<Column
				field="relation.name"
				header="Relation"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={relationTemplate}
			/>
			<Column
				field="componentSymbol"
				header="Component Symbol"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="taxon.name"
				header="Taxon"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={taxonTemplate}
			/>
			<Column
				field="taxonText"
				header="Taxon Text"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				header="Related Notes"
				body={relatedNotesTemplate}
			/>
			<Column
				header="Evidence"
				body={evidenceTemplate}
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
		</DataTable>
	);
};
