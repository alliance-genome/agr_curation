import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

const termTemplate = (term) => {
	if (!term) return null;
	if (!term.name) return term.curie || '';
	if (term.curie) return `${term.name} (${term.curie})`;
	if (term.definition) return `${term.name} (${term.definition})`;
	return term.name;
};

const listTemplate = (terms) => {
	if (!terms || terms.length === 0) return null;
	return terms.map((t) => termTemplate(t)).join(', ');
};

export const WhenExpressedDialog = ({ whenExpressedData, setWhenExpressedData }) => {
	const { data, statement, dialog } = whenExpressedData;
	const [localData, setLocalData] = useState(null);

	const showDialogHandler = () => {
		if (data) {
			const _localData = structuredClone(data);
			_localData.dataKey = 0;
			_localData.whenExpressedStageName = statement;
			setLocalData([_localData]);
		} else {
			setLocalData([]);
		}
	};

	const hideDialog = () => {
		setWhenExpressedData((prev) => ({
			...prev,
			dialog: false,
		}));
		setLocalData(null);
	};

	return (
		<Dialog visible={dialog} className="w-10" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>When Expressed [Temporal Context]</h3>
			<DataTable value={localData} dataKey="dataKey" showGridlines>
				<Column
					field="whenExpressedStageName"
					header="When Expressed Statement"
					body={(rowData) => <EllipsisTableCell>{rowData.whenExpressedStageName}</EllipsisTableCell>}
				/>
				<Column
					field="developmentalStageStart.name"
					header="Developmental Stage Start"
					body={(rowData) => <EllipsisTableCell>{termTemplate(rowData.developmentalStageStart)}</EllipsisTableCell>}
				/>
				<Column
					field="developmentalStageStop.name"
					header="Developmental Stage Stop"
					body={(rowData) => <EllipsisTableCell>{termTemplate(rowData.developmentalStageStop)}</EllipsisTableCell>}
				/>
				<Column field="age" header="Age" body={(rowData) => <EllipsisTableCell>{rowData.age}</EllipsisTableCell>} />
				<Column
					field="temporalQualifiers"
					header="Temporal Qualifiers"
					body={(rowData) => <EllipsisTableCell>{listTemplate(rowData.temporalQualifiers)}</EllipsisTableCell>}
				/>
				<Column
					field="stageUberonSlimTerms"
					header="Stage Uberon Terms"
					body={(rowData) => <EllipsisTableCell>{listTemplate(rowData.stageUberonSlimTerms)}</EllipsisTableCell>}
				/>
				<Column
					field="updatedBy.uniqueId"
					header="Updated By"
					body={(rowData) => <EllipsisTableCell>{rowData.updatedBy?.uniqueId}</EllipsisTableCell>}
				/>
				<Column
					field="dateUpdated"
					header="Date Updated"
					body={(rowData) => <EllipsisTableCell>{rowData.dateUpdated}</EllipsisTableCell>}
				/>
				<Column
					field="createdBy.uniqueId"
					header="Created By"
					body={(rowData) => <EllipsisTableCell>{rowData.createdBy?.uniqueId}</EllipsisTableCell>}
				/>
				<Column
					field="dateCreated"
					header="Date Created"
					body={(rowData) => <EllipsisTableCell>{rowData.dateCreated}</EllipsisTableCell>}
				/>
			</DataTable>
		</Dialog>
	);
};
