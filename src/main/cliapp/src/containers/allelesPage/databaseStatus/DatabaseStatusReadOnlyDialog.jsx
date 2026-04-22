import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const DatabaseStatusReadOnlyDialog = ({ originalDatabaseStatusData, setOriginalDatabaseStatusData }) => {
	const { originalDatabaseStatuses, isInEdit, dialog } = originalDatabaseStatusData;
	const [localDatabaseStatuses, setLocalDatabaseStatuses] = useState(null);

	const showDialogHandler = () => {
		let _localDatabaseStatuses = [];
		if (originalDatabaseStatuses?.length > 0 && originalDatabaseStatuses[0]) {
			_localDatabaseStatuses = structuredClone(originalDatabaseStatuses);
			let counter = 0;
			_localDatabaseStatuses.forEach((ds) => {
				ds.dataKey = counter++;
			});
		}
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const hideDialog = () => {
		setOriginalDatabaseStatusData((originalDatabaseStatusData) => {
			return {
				...originalDatabaseStatusData,
				dialog: false,
			};
		});
		setLocalDatabaseStatuses([]);
	};

	const databaseStatusTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.databaseStatus?.name}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-5" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Database Status</h3>
			<DataTable value={localDatabaseStatuses} dataKey="dataKey" showGridlines>
				<Column field="databaseStatus" header="Database Status" body={databaseStatusTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
