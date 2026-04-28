import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { ListTableCell } from '../../../components/ListTableCell';
import { evidenceTemplate } from '../../../components/EvidenceComponent';

export const MutationTypesReadOnlyDialog = ({ originalMutationTypesData, setOriginalMutationTypesData }) => {
	const { originalMutationTypes, isInEdit, dialog } = originalMutationTypesData;
	const [localMutationTypes, setLocalMutationTypes] = useState(null);

	const showDialogHandler = () => {
		let _localMutationTypes = [];
		if (originalMutationTypes) {
			_localMutationTypes = structuredClone(originalMutationTypes);
			let counter = 0;
			_localMutationTypes.forEach((mt) => {
				mt.dataKey = counter++;
			});
		}
		setLocalMutationTypes(_localMutationTypes);
	};

	const hideDialog = () => {
		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
				dialog: false,
			};
		});
		setLocalMutationTypes([]);
	};

	const mutationTypeTemplate = (rowData) => {
		if (rowData && rowData.mutationTypes) {
			const sortedMutationTypes = rowData.mutationTypes.sort((a, b) => (a.name > b.name ? 1 : -1));
			const listTemplate = (item) => {
				return <EllipsisTableCell>{item.name + ' (' + item.curie + ')'}</EllipsisTableCell>;
			};
			return <ListTableCell template={listTemplate} listData={sortedMutationTypes} />;
		}
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-6" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Mutation Types</h3>
			<DataTable value={localMutationTypes} dataKey="dataKey" showGridlines>
				<Column field="mutationType.curie" header="Mutation Types" body={mutationTypeTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
			</DataTable>
		</Dialog>
	);
};
