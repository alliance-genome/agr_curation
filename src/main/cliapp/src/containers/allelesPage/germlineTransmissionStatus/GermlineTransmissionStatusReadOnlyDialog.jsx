import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const GermlineTransmissionStatusReadOnlyDialog = ({
	originalGermlineTransmissionStatusData,
	setOriginalGermlineTransmissionStatusData,
}) => {
	const { originalGermlineTransmissionStatuses, isInEdit, dialog } = originalGermlineTransmissionStatusData;
	const [localGermlineTransmissionStatuses, setLocalGermlineTransmissionStatuses] = useState(null);

	const showDialogHandler = () => {
		let _localGermlineTransmissionStatuses = [];
		if (originalGermlineTransmissionStatuses?.length > 0 && originalGermlineTransmissionStatuses[0]) {
			_localGermlineTransmissionStatuses = structuredClone(originalGermlineTransmissionStatuses);
			let counter = 0;
			_localGermlineTransmissionStatuses.forEach((gts) => {
				gts.dataKey = counter++;
			});
		}
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const hideDialog = () => {
		setOriginalGermlineTransmissionStatusData((originalGermlineTransmissionStatusData) => {
			return {
				...originalGermlineTransmissionStatusData,
				dialog: false,
			};
		});
		setLocalGermlineTransmissionStatuses([]);
	};

	const germlineTransmissionStatusTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.germlineTransmissionStatus?.name}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-5" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Germline Transmission Status</h3>
			<DataTable value={localGermlineTransmissionStatuses} dataKey="dataKey" showGridlines>
				<Column
					field="germlineTransmissionStatus"
					header="Germline Transmission Status"
					body={germlineTransmissionStatusTemplate}
				/>
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
