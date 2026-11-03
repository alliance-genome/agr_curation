import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';

/**
 * Read-only popup for a sample's BioSampleAge record.
 */
export const BioSampleAgeReadOnlyDialog = ({ originalEntitiesData, setOriginalEntitiesData }) => {
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
			<h3>Sample Age</h3>
			<DataTable value={localEntities} dataKey="id" showGridlines>
				<Column field="age" header="Age" body={(rowData) => <StringTemplate string={rowData.age} />} />
				<Column field="stage.age" header="Stage" body={(rowData) => <StringTemplate string={rowData.stage?.age} />} />
				<Column
					field="whenExpressedStageName"
					header="When Expressed Stage Name"
					body={(rowData) => <StringTemplate string={rowData.whenExpressedStageName} />}
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
