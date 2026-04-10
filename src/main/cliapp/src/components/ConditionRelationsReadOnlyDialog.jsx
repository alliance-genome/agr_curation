import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from './EllipsisTableCell';
import { ListTableCell } from './ListTableCell';

export const ConditionRelationsReadOnlyDialog = ({
	originalConditionRelationsData,
	setOriginalConditionRelationsData,
}) => {
	const { originalConditionRelations, isInEdit, dialog } = originalConditionRelationsData;
	const [localConditionRelations, setLocalConditionRelations] = useState(null);

	const showDialogHandler = () => {
		let _localConditionRelations = [];
		if (originalConditionRelations) {
			_localConditionRelations = structuredClone(originalConditionRelations);
			let counter = 0;
			_localConditionRelations.forEach((relation) => {
				relation.dataKey = counter++;
			});
		}
		setLocalConditionRelations(_localConditionRelations);
	};

	const hideDialog = () => {
		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
				dialog: false,
			};
		});
		setLocalConditionRelations([]);
	};

	const conditionRelationTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.conditionRelationType?.name}</EllipsisTableCell>;
	};

	const conditionsTemplate = (rowData) => {
		if (rowData && rowData.conditions) {
			const sortedConditionSummaries = rowData.conditions.sort((a, b) =>
				a.conditionSummary > b.conditionSummary ? 1 : -1
			);
			const listTemplate = (item) => {
				return <EllipsisTableCell>{item.conditionSummary + ' (' + item.uniqueId + ')'}</EllipsisTableCell>;
			};
			return <ListTableCell template={listTemplate} listData={sortedConditionSummaries} />;
		}
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog
			visible={dialog && !isInEdit}
			className="w-10 min-w-40rem"
			modal
			onHide={hideDialog}
			closable
			onShow={showDialogHandler}
		>
			<h3>Experimental Conditions</h3>
			<DataTable value={localConditionRelations} dataKey="dataKey" showGridlines>
				<Column field="conditionRelationType.name" header="Relation" body={conditionRelationTypeTemplate} />
				<Column field="conditions.conditionSummary" header="Conditions" body={conditionsTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
			</DataTable>
		</Dialog>
	);
};
