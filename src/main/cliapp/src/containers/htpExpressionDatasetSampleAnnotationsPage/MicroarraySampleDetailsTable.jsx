import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only, always-expanded table for a sample's MicroarraySampleDetails record - same columns
 * as MicroarraySampleDetailsReadOnlyDialog, but shown inline on the detail page rather than
 * behind a pop-up. The mockup also shows Curie, Primary External ID, MOD Internal ID, Notes and
 * Data Provider, but none of those exist on the backend entity yet (channelId/channelNumber
 * only) - omitted rather than invented.
 */
export const MicroarraySampleDetailsTable = ({ entities }) => {
	return (
		<DataTable value={entities || []} dataKey="id" showGridlines emptyMessage="No data available">
			<Column field="channelId" header="Channel ID" body={(rowData) => <StringTemplate string={rowData.channelId} />} />
			<Column
				field="channelNumber"
				header="Channel Number"
				body={(rowData) => <StringTemplate string={rowData.channelNumber} />}
			/>
			<Column field="updatedBy.uniqueId" header="Updated By" />
			<Column field="dateUpdated" header="Date Updated" />
			<Column field="createdBy.uniqueId" header="Created By" />
			<Column field="dateCreated" header="Date Created" />
			<Column field="internal" header="Internal" body={(rowData) => <BooleanTemplate value={rowData.internal} />} />
			<Column field="obsolete" header="Obsolete" body={(rowData) => <BooleanTemplate value={rowData.obsolete} />} />
		</DataTable>
	);
};
