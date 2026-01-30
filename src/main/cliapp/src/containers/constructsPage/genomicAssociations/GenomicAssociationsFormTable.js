import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tooltip } from 'primereact/tooltip';
import { getIdentifier } from '../../../utils/utils';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const GenomicAssociationsFormTable = ({ associations, tableRef }) => {
	const relationTemplate = (rowData) => {
		if (!rowData?.relation?.name) return null;
		let relationName = rowData.relation.name;
		// Remove RO: suffix if present
		if (relationName.indexOf(' (RO:') !== -1) {
			relationName = relationName.substring(0, relationName.indexOf(' (RO:'));
		}
		return relationName;
	};

	const componentTemplate = (rowData) => {
		if (!rowData?.constructGenomicEntityAssociationObject) return null;

		let componentDisplayValue = '';
		const obj = rowData.constructGenomicEntityAssociationObject;

		if (obj.geneSymbol || obj.alleleSymbol) {
			let symbolValue = obj.geneSymbol ? obj.geneSymbol.displayText : obj.alleleSymbol.displayText;
			componentDisplayValue = symbolValue + ' (' + getIdentifier(obj) + ')';
		} else if (obj.name) {
			componentDisplayValue = obj.name + ' (' + getIdentifier(obj) + ')';
		} else {
			componentDisplayValue = getIdentifier(obj);
		}

		return (
			<>
				<div
					className={`overflow-hidden text-overflow-ellipsis component_${rowData.id}`}
					dangerouslySetInnerHTML={{ __html: componentDisplayValue }}
				/>
				<Tooltip target={`.component_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: componentDisplayValue }} />
				</Tooltip>
			</>
		);
	};

	const relatedNotesTemplate = (rowData) => {
		if (!rowData?.relatedNotes || rowData.relatedNotes.length === 0) return null;
		return `Notes(${rowData.relatedNotes.length})`;
	};

	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

	const obsoleteTemplate = (rowData) => {
		return rowData?.obsolete?.toString() || 'false';
	};

	return (
		<DataTable
			value={associations}
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
			emptyMessage="No genomic entity associations available"
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
				field="constructGenomicEntityAssociationObject.primaryExternalId"
				header="Component"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={componentTemplate}
			/>
			<Column header="Related Notes" body={relatedNotesTemplate} />
			<Column header="Evidence" body={evidenceTemplate} />
			<Column
				field="updatedBy.uniqueId"
				header="Updated By"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="dateUpdated"
				header="Date Updated"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="createdBy.uniqueId"
				header="Created By"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
			/>
			<Column
				field="dateCreated"
				header="Date Created"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
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
			<Column
				field="obsolete"
				header="Obsolete"
				sortable
				filter
				showFilterMenu={false}
				filterMatchMode="contains"
				body={obsoleteTemplate}
			/>
		</DataTable>
	);
};
