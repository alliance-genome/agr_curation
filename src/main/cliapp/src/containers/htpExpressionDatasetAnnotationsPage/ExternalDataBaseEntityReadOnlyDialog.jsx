import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';

/**
 * Read-only popup for one or more ExternalDataBaseEntity records (an HTP dataset's own identity,
 * or its subseries - the same shape, a title tells them apart).
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
		<Dialog visible={!!dialog} className="w-8" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>{title}</h3>
			<DataTable value={localEntities} dataKey="id" showGridlines>
				<Column field="curie" header="Curie" />
				<Column
					field="secondaryIdentifiers"
					header="Secondary Identifiers"
					body={(rowData) => <StringListTemplate list={rowData.secondaryIdentifiers} />}
				/>
				<Column
					field="crossReferences"
					header="Cross References"
					body={(rowData) => <CrossReferencesTemplate list={rowData.crossReferences} />}
				/>
			</DataTable>
		</Dialog>
	);
};
