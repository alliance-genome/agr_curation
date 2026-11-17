import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only popup for a sample's MicroarraySampleDetails record. The mockup also shows Curie,
 * Primary External ID, MOD Internal ID, Notes and Data Provider, but none of those exist on the
 * backend entity yet (channelId/channelNumber only) - omitted rather than invented.
 */
export const MicroarraySampleDetailsReadOnlyDialog = ({ originalEntitiesData, setOriginalEntitiesData }) => {
	const { originalEntities, dialog } = originalEntitiesData;
	const [localEntities, setLocalEntities] = useState(null);

	const showDialogHandler = () => {
		setLocalEntities(originalEntities?.length > 0 ? originalEntities : []);
	};

	const hideDialog = () => {
		setOriginalEntitiesData((originalEntitiesData) => {
			return {
				...originalEntitiesData,
				dialog: false,
			};
		});
		setLocalEntities([]);
	};

	return (
		<Dialog visible={!!dialog} className="w-9" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Microarray Sample Details</h3>
			<DataTable value={localEntities} dataKey="id" showGridlines>
				<Column
					field="channelId"
					header="Channel ID"
					body={(rowData) => <StringTemplate string={rowData.channelId} />}
				/>
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
		</Dialog>
	);
};
