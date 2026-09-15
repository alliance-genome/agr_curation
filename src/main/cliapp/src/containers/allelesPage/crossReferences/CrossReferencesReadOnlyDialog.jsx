import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';

export const CrossReferencesReadOnlyDialog = ({ originalCrossReferencesData, setOriginalCrossReferencesData }) => {
	const { originalCrossReferences, isInEdit, dialog } = originalCrossReferencesData;
	const [localCrossReferences, setLocalCrossReferences] = useState([]);

	const showDialogHandler = () => {
		const clonedCrossReferences = structuredClone(originalCrossReferences) ?? [];
		clonedCrossReferences.forEach((crossReference, index) => {
			crossReference.dataKey = index;
		});
		setLocalCrossReferences(clonedCrossReferences);
	};

	const hideDialog = () => {
		setOriginalCrossReferencesData((previous) => ({ ...previous, dialog: false }));
		setLocalCrossReferences([]);
	};

	const textTemplate = (value) => <EllipsisTableCell>{value}</EllipsisTableCell>;

	return (
		<Dialog visible={dialog && !isInEdit} className="w-8" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Cross References</h3>
			<DataTable value={localCrossReferences} dataKey="dataKey" showGridlines>
				<Column field="displayName" header="Display Name" body={(rowData) => textTemplate(rowData.displayName)} />
				<Column
					field="resourceDescriptorPage.resourceDescriptor.prefix"
					header="Resource Descriptor"
					body={(rowData) => textTemplate(rowData.resourceDescriptorPage?.resourceDescriptor?.prefix)}
				/>
				<Column
					field="referencedCurie"
					header="Referenced Curie"
					body={(rowData) => textTemplate(rowData.referencedCurie)}
				/>
				<Column
					field="resourceDescriptorPage.name"
					header="Resource Descriptor Page"
					body={(rowData) => textTemplate(rowData.resourceDescriptorPage?.name)}
				/>
				<Column field="internal" header="Internal" body={(rowData) => textTemplate(JSON.stringify(rowData.internal))} />
				<Column field="obsolete" header="Obsolete" body={(rowData) => textTemplate(JSON.stringify(rowData.obsolete))} />
			</DataTable>
		</Dialog>
	);
};
