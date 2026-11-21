import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { StringTemplate } from '../../../components/Templates/StringTemplate';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';
import { getRefStrings } from '../../../utils/utils';

export const SymbolFormTable = ({ symbol, tableRef }) => {
	const headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Display Text" />
				<Column header="Format Text" />
				<Column header="Synonym Scope" />
				<Column header="Name Type" />
				<Column header="Synonym URL" />
				<Column header="Internal" />
				<Column header="Evidence" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
			</Row>
		</ColumnGroup>
	);

	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

	return (
		<DataTable
			value={symbol}
			dataKey="id"
			showGridlines
			headerColumnGroup={headerGroup}
			size="small"
			resizableColumns
			columnResizeMode="expand"
			ref={tableRef}
		>
			<Column
				field="displayText"
				header="Display Text"
				headerClassName="surface-0"
				body={(rowData) => <StringTemplate string={rowData.displayText} />}
			/>
			<Column
				field="formatText"
				header="Format Text"
				headerClassName="surface-0"
				body={(rowData) => <EllipsisTableCell>{rowData.formatText}</EllipsisTableCell>}
			/>
			<Column
				field="synonymScope"
				header="Synonym Scope"
				headerClassName="surface-0"
				body={(rowData) => <StringTemplate string={rowData.synonymScope?.name} />}
			/>
			<Column
				field="nameType"
				header="Name Type"
				headerClassName="surface-0"
				body={(rowData) => <StringTemplate string={rowData.nameType?.name} />}
			/>
			<Column
				field="synonymUrl"
				header="Synonym URL"
				headerClassName="surface-0"
				body={(rowData) => <StringTemplate string={rowData.synonymUrl} />}
			/>
			<Column field="internal" header="Internal" body={internalTemplate} headerClassName="surface-0" />
			<Column
				field="evidence.curie"
				header="Evidence"
				headerClassName="surface-0"
				body={(rowData) => <StringListTemplate list={getRefStrings(rowData.evidence)} />}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
			<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
		</DataTable>
	);
};
