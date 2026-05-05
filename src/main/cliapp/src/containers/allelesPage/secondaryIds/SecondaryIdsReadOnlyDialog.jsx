import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const SecondaryIdsReadOnlyDialog = ({ originalSecondaryIdsData, setOriginalSecondaryIdsData }) => {
	const { originalSecondaryIds, isInEdit, dialog } = originalSecondaryIdsData;
	const [localSecondaryIds, setLocalSecondaryIds] = useState(null);

	const showDialogHandler = () => {
		let _localSecondaryIds = [];
		if (originalSecondaryIds) {
			_localSecondaryIds = structuredClone(originalSecondaryIds);
			let counter = 0;
			_localSecondaryIds.forEach((sid) => {
				sid.dataKey = counter++;
			});
		}
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const hideDialog = () => {
		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
				dialog: false,
			};
		});
		setLocalSecondaryIds([]);
	};

	const secondaryIdTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.secondaryId}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-6" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Secondary IDs</h3>
			<DataTable value={localSecondaryIds} dataKey="dataKey" showGridlines>
				<Column field="secondaryId" header="Secondary ID" body={secondaryIdTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
