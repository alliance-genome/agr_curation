import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';

export const FullNameFormTable = ({ name, tableRef }) => {
	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Display Text" />
				<Column header="Format Text" />
				<Column header="Name Type" />
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

	return (
		<DataTable
			value={name}
			dataKey="dataKey"
			showGridlines
			headerColumnGroup={headerGroup}
			size="small"
			resizableColumns
			columnResizeMode="expand"
			ref={tableRef}
		>
			<Column field="displayText" header="Display Text" headerClassName="surface-0" />
			<Column field="formatText" header="Format Text" headerClassName="surface-0" />
			<Column field="nameType.name" header="Name Type" headerClassName="surface-0" />
			<Column field="internal" header="Internal" headerClassName="surface-0" body={internalTemplate} />
		</DataTable>
	);
};
