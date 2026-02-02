import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import {
	synonymScopeTemplate,
	nameTypeTemplate,
	synonymUrlTemplate,
	displayTextTemplate,
	formatTextTemplate,
} from '../../../components/NameSlotAnnotationComponent';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const FullNameFormTable = ({ name, tableRef }) => {
	const internalTemplate = (rowData) => {
		return rowData?.internal?.toString() || 'false';
	};

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
			<Column field="displayText" header="Display Text" headerClassName="surface-0" body={displayTextTemplate} />
			<Column
				field="formatText"
				header="Format Text"
				headerClassName="surface-0"
				body={formatTextTemplate}
			/>
			<Column
				field="synonymScope"
				header="Synonym Scope"
				headerClassName="surface-0"
				body={synonymScopeTemplate}
			/>
			<Column
				field="nameType"
				header="Name Type"
				headerClassName="surface-0"
				body={nameTypeTemplate}
			/>
			<Column
				field="synonymUrl"
				header="Synonym URL"
				headerClassName="surface-0"
				body={synonymUrlTemplate}
			/>
			<Column
				field="internal"
				header="Internal"
				body={internalTemplate}
				headerClassName="surface-0"
			/>
			<Column
				field="evidence.curie"
				header="Evidence"
				headerClassName="surface-0"
				body={evidenceTemplate}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
			<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
		</DataTable>
	);
};
