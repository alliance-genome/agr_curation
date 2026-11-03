import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from './Templates/StringListTemplate';
import { CrossReferenceTemplate } from './Templates/CrossReferenceTemplate';
import { CrossReferencesTemplate } from './Templates/CrossReferencesTemplate';
import { BooleanTemplate } from './Templates/BooleanTemplate';

/**
 * Read-only popup for one or more ExternalDataBaseEntity records (an HTP dataset's own identity,
 * its subseries, an HTP sample's own identity, or its biological reference - the same shape, a
 * title tells them apart).
 */
export const ExternalDataBaseEntityReadOnlyDialog = ({ originalEntitiesData, setOriginalEntitiesData, title }) => {
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
		<Dialog visible={!!dialog} className="w-11" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>{title}</h3>
			<DataTable value={localEntities} dataKey="id" showGridlines>
				<Column field="curie" header="Curie" />
				<Column
					field="preferredCrossReference"
					header="Preferred Cross Reference"
					body={(rowData) => <CrossReferenceTemplate xref={rowData.preferredCrossReference} />}
				/>
				<Column
					field="crossReferences"
					header="Cross References"
					body={(rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />}
				/>
				<Column
					field="secondaryIdentifiers"
					header="Secondary Identifiers"
					body={(rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />}
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
