import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { ListTableCell } from '../../../components/ListTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const FunctionalImpactsReadOnlyDialog = ({
	originalFunctionalImpactsData,
	setOriginalFunctionalImpactsData,
}) => {
	const { originalFunctionalImpacts, isInEdit, dialog } = originalFunctionalImpactsData;
	const [localFunctionalImpacts, setLocalFunctionalImpacts] = useState(null);

	const showDialogHandler = () => {
		let _localFunctionalImpacts = [];
		if (originalFunctionalImpacts) {
			_localFunctionalImpacts = structuredClone(originalFunctionalImpacts);
			let counter = 0;
			_localFunctionalImpacts.forEach((fi) => {
				fi.dataKey = counter++;
			});
		}
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const hideDialog = () => {
		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
				dialog: false,
			};
		});
		setLocalFunctionalImpacts([]);
	};

	const functionalImpactsTemplate = (rowData) => {
		if (rowData && rowData.functionalImpacts) {
			const sortedFunctionalImpacts = rowData.functionalImpacts.sort((a, b) => (a.name > b.name ? 1 : -1));
			const listTemplate = (item) => item.name;
			return <ListTableCell template={listTemplate} listData={sortedFunctionalImpacts} />;
		}
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
			<h3>Functional Impacts</h3>
			<DataTable value={localFunctionalImpacts} dataKey="dataKey" showGridlines>
				<Column field="functionalImpacts.name" header="Functional Impacts" body={functionalImpactsTemplate} />
				<Column field="phenotypeTerm.curie" header="Phenotype Term" body={phenotypeTermTemplate} />
				<Column field="phenotypeStatement" header="Phenotype Statement" body={phenotypeStatementTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
