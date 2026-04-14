import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

const termTemplate = (term) => {
	if (!term) return null;
	if (!term.name) return term.curie || '';
	if (!term.curie) return term.name;
	return `${term.name} (${term.curie})`;
};

const listTemplate = (terms) => {
	if (!terms || terms.length === 0) return null;
	return terms.map((t) => termTemplate(t)).join(', ');
};

export const WhereExpressedDialog = ({ whereExpressedData, setWhereExpressedData }) => {
	const { data, statement, dialog } = whereExpressedData;
	const [localData, setLocalData] = useState(null);

	const showDialogHandler = () => {
		if (data) {
			const _localData = structuredClone(data);
			_localData.dataKey = 0;
			_localData.whereExpressedStatement = statement;
			setLocalData([_localData]);
		} else {
			setLocalData([]);
		}
	};

	const hideDialog = () => {
		setWhereExpressedData((prev) => ({
			...prev,
			dialog: false,
		}));
		setLocalData(null);
	};

	return (
		<Dialog visible={dialog} className="w-10" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Where Expressed [Anatomical Site]</h3>
			<DataTable value={localData} dataKey="dataKey" showGridlines>
				<Column
					field="whereExpressedStatement"
					header="Where Expressed Statement"
					body={(rowData) => <EllipsisTableCell>{rowData.whereExpressedStatement}</EllipsisTableCell>}
				/>
				<Column
					field="anatomicalStructure.name"
					header="Anatomical Structure"
					body={(rowData) => <EllipsisTableCell>{termTemplate(rowData.anatomicalStructure)}</EllipsisTableCell>}
				/>
				<Column
					field="anatomicalStructureQualifiers"
					header="Structure Qualifiers"
					body={(rowData) => (
						<EllipsisTableCell>{listTemplate(rowData.anatomicalStructureQualifiers)}</EllipsisTableCell>
					)}
				/>
				<Column
					field="anatomicalStructureUberonTerms"
					header="Structure Uberon Terms"
					body={(rowData) => (
						<EllipsisTableCell>{listTemplate(rowData.anatomicalStructureUberonTerms)}</EllipsisTableCell>
					)}
				/>
				<Column
					field="anatomicalSubstructure.name"
					header="Anatomical Substructure"
					body={(rowData) => <EllipsisTableCell>{termTemplate(rowData.anatomicalSubstructure)}</EllipsisTableCell>}
				/>
				<Column
					field="anatomicalSubstructureQualifiers"
					header="Substructure Qualifiers"
					body={(rowData) => (
						<EllipsisTableCell>{listTemplate(rowData.anatomicalSubstructureQualifiers)}</EllipsisTableCell>
					)}
				/>
				<Column
					field="anatomicalSubstructureUberonTerms"
					header="Substructure Uberon Terms"
					body={(rowData) => (
						<EllipsisTableCell>{listTemplate(rowData.anatomicalSubstructureUberonTerms)}</EllipsisTableCell>
					)}
				/>
				<Column
					field="cellularComponentTerm.name"
					header="Cellular Component"
					body={(rowData) => <EllipsisTableCell>{termTemplate(rowData.cellularComponentTerm)}</EllipsisTableCell>}
				/>
				<Column
					field="cellularComponentQualifiers"
					header="Cellular Component Qualifiers"
					body={(rowData) => <EllipsisTableCell>{listTemplate(rowData.cellularComponentQualifiers)}</EllipsisTableCell>}
				/>
				<Column
					field="cellularComponentRibbonTerms"
					header="CC Ribbon Terms"
					body={(rowData) => (
						<EllipsisTableCell>{listTemplate(rowData.cellularComponentRibbonTerms)}</EllipsisTableCell>
					)}
				/>
				<Column
					field="cellularComponentOther"
					header="CC Other"
					body={(rowData) => (
						<EllipsisTableCell>
							{rowData.cellularComponentOther != null ? String(rowData.cellularComponentOther) : ''}
						</EllipsisTableCell>
					)}
				/>
				<Column
					field="anatomicalStructureUberonTermOther"
					header="Structure Uberon Other"
					body={(rowData) => (
						<EllipsisTableCell>
							{rowData.anatomicalStructureUberonTermOther != null
								? String(rowData.anatomicalStructureUberonTermOther)
								: ''}
						</EllipsisTableCell>
					)}
				/>
				<Column
					field="anatomicalSubStructureUberonTermOther"
					header="Substructure Uberon Other"
					body={(rowData) => (
						<EllipsisTableCell>
							{rowData.anatomicalSubStructureUberonTermOther != null
								? String(rowData.anatomicalSubStructureUberonTermOther)
								: ''}
						</EllipsisTableCell>
					)}
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
