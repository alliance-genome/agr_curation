import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { useTermTexts } from './utils';

// The uses of a transgenic tool, one row per use slot annotation: its terms, evidence and audit
// fields. Shown by the table's uses pop-up and on the detail page.
export const TransgenicToolUsesTable = ({ uses }) => {
	const usesTemplate = (rowData) => (
		<div className="flex flex-column">
			{useTermTexts(rowData).map((text, index) => (
				<div key={index}>{text}</div>
			))}
		</div>
	);

	const booleanTemplate = (value) => <EllipsisTableCell>{JSON.stringify(value)}</EllipsisTableCell>;

	return (
		<DataTable value={uses || []} dataKey="id" showGridlines>
			<Column field="uses.name" header="Uses" headerClassName="surface-0" body={usesTemplate} />
			<Column
				field="evidence.curie"
				header="Evidence"
				headerClassName="surface-0"
				body={(rowData) => evidenceTemplate(rowData)}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
			<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
			<Column field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />
			<Column field="dateCreated" header="Date Created" headerClassName="surface-0" />
			<Column
				field="internal"
				header="Internal"
				headerClassName="surface-0"
				body={(rowData) => booleanTemplate(rowData.internal)}
			/>
			<Column
				field="obsolete"
				header="Obsolete"
				headerClassName="surface-0"
				body={(rowData) => booleanTemplate(rowData.obsolete)}
			/>
		</DataTable>
	);
};
