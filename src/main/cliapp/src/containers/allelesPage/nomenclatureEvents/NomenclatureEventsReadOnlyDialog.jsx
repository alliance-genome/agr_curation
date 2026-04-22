import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const NomenclatureEventsReadOnlyDialog = ({
	originalNomenclatureEventsData,
	setOriginalNomenclatureEventsData,
}) => {
	const { originalNomenclatureEvents, isInEdit, dialog } = originalNomenclatureEventsData;
	const [localNomenclatureEvents, setLocalNomenclatureEvents] = useState(null);

	const showDialogHandler = () => {
		let _localNomenclatureEvents = [];
		if (originalNomenclatureEvents) {
			_localNomenclatureEvents = structuredClone(originalNomenclatureEvents);
			let counter = 0;
			_localNomenclatureEvents.forEach((ne) => {
				ne.dataKey = counter++;
			});
		}
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const hideDialog = () => {
		setOriginalNomenclatureEventsData((originalNomenclatureEventsData) => {
			return {
				...originalNomenclatureEventsData,
				dialog: false,
			};
		});
		setLocalNomenclatureEvents([]);
	};

	const nomenclatureEventTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.nomenclatureEvent?.name}</EllipsisTableCell>;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const obsoleteTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.obsolete)}</EllipsisTableCell>;
	};

	return (
		<Dialog
			visible={dialog && !isInEdit}
			className="w-10"
			modal
			onHide={hideDialog}
			closable
			onShow={showDialogHandler}
		>
			<h3>Nomenclature Events</h3>
			<DataTable value={localNomenclatureEvents} dataKey="dataKey" showGridlines>
				<Column field="nomenclatureEvent.name" header="Nomenclature Event" body={nomenclatureEventTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="obsolete" header="Obsolete" body={obsoleteTemplate} />
				<Column field="updatedBy.uniqueId" header="Updated By" />
				<Column field="dateUpdated" header="Date Updated" />
				<Column field="createdBy.uniqueId" header="Created By" />
				<Column field="dateCreated" header="Date Created" />
			</DataTable>
		</Dialog>
	);
};
