import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const InheritanceModesReadOnlyDialog = ({ originalInheritanceModesData, setOriginalInheritanceModesData }) => {
	const { originalInheritanceModes, isInEdit, dialog } = originalInheritanceModesData;
	const [localInheritanceModes, setLocalInheritanceModes] = useState(null);

	const showDialogHandler = () => {
		let _localInheritanceModes = [];
		if (originalInheritanceModes) {
			_localInheritanceModes = structuredClone(originalInheritanceModes);
			let counter = 0;
			_localInheritanceModes.forEach((im) => {
				im.dataKey = counter++;
			});
		}
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const hideDialog = () => {
		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
				dialog: false,
			};
		});
		setLocalInheritanceModes([]);
	};

	const inheritanceModeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.inheritanceMode?.name}</EllipsisTableCell>;
	};

	const phenotypeTermTemplate = (rowData) => {
		if (rowData?.phenotypeTerm) {
			return (
				<EllipsisTableCell>{rowData.phenotypeTerm.name + ' (' + rowData.phenotypeTerm.curie + ')'}</EllipsisTableCell>
			);
		}
	};

	const phenotypeStatementTemplate = (rowData) => {
		if (rowData?.phenotypeStatement) {
			return <EllipsisTableCell>{rowData.phenotypeStatement}</EllipsisTableCell>;
		}
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-6" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Inheritance Modes</h3>
			<DataTable value={localInheritanceModes} dataKey="dataKey" showGridlines>
				<Column field="inheritanceMode.name" header="Inheritance Mode" body={inheritanceModeTemplate} />
				<Column field="phenotypeTerm.curie" header="Phenotype Term" body={phenotypeTermTemplate} />
				<Column field="phenotypeStatement" header="Phenotype Statement" body={phenotypeStatementTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
