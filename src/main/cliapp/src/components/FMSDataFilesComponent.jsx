import React, { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export const FMSDataFilesComponent = ({ dataFiles }) => {
	const [expandedRows, setExpandedRows] = useState(null);

	const downloadTemplate = (row) => {
		return <a href={row.s3Url}>Download</a>;
	};

	const dataFileTable = (row) => {
		console.log(row);
		return (
			<DataTable
				value={row.dataFiles}
				className="p-datatable-sm"
				filterDisplay="row"
				sortField="uploadDate"
				sortOrder={-1}
			>
				<Column showFilterMenu={false} field="dataSubType.name" header="Sub Type" sortable filter></Column>
				<Column showFilterMenu={false} field="uploadDate" header="Upload Date" sortable filter></Column>
				<Column showFilterMenu={false} field="md5Sum" header="MD5" sortable filter></Column>
				<Column showFilterMenu={false} field="schemaVersion.schema" header="Schema" sortable filter></Column>
				<Column showFilterMenu={false} field="dataType.fileExtension" header="Ext" sortable filter></Column>
				<Column showFilterMenu={false} body={downloadTemplate} header="S3 Download" sortable filter></Column>
			</DataTable>
		);
	};

	return (
		<DataTable
			value={dataFiles}
			className="p-datatable-sm"
			filterDisplay="row"
			sortField="releaseVersion"
			expandedRows={expandedRows}
			onRowToggle={(e) => setExpandedRows(e.data)}
			rowExpansionTemplate={dataFileTable}
			sortOrder={-1}
			paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
		>
			<Column expander style={{ width: '3em' }} />
			<Column showFilterMenu={false} field="releaseVersion" header="Releases" sortable filter></Column>
			<Column showFilterMenu={false} field="releaseDate" header="Release Date" sortable filter></Column>
		</DataTable>
	);
};
