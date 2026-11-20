import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';
import { getRefStrings } from '../../../utils/utils';
import { StringTemplate } from '../../../components/Templates/StringTemplate';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';

export const FullNameReadOnlyDialog = ({ originalFullNameData, setOriginalFullNameData }) => {
	const { originalFullNames, isInEdit, dialog } = originalFullNameData;
	const [localFullNames, setLocalFullNames] = useState(null);

	const showDialogHandler = () => {
		let _localFullNames = [];
		if (originalFullNames?.length > 0 && originalFullNames[0]) {
			_localFullNames = structuredClone(originalFullNames);
			let counter = 0;
			_localFullNames.forEach((fn) => {
				fn.dataKey = counter++;
			});
		}
		setLocalFullNames(_localFullNames);
	};

	const hideDialog = () => {
		setOriginalFullNameData((originalFullNameData) => {
			return {
				...originalFullNameData,
				dialog: false,
			};
		});
		setLocalFullNames([]);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
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
			<h3>Full Name</h3>
			<DataTable value={localFullNames} dataKey="dataKey" showGridlines>
				<Column
					field="displayText"
					header="Display Text"
					body={(rowData) => <StringTemplate string={rowData.displayText} />}
				/>
				<Column
					field="formatText"
					header="Format Text"
					body={(rowData) => <EllipsisTableCell>{rowData.formatText}</EllipsisTableCell>}
				/>
				<Column
					field="synonymScope"
					header="Synonym Scope"
					body={(rowData) => <StringTemplate string={rowData.synonymScope?.name} />}
				/>
				<Column
					field="nameType"
					header="Name Type"
					body={(rowData) => <StringTemplate string={rowData.nameType?.name} />}
				/>
				<Column
					field="synonymUrl"
					header="Synonym URL"
					body={(rowData) => <StringTemplate string={rowData.synonymUrl} />}
				/>
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column
					field="evidence.curie"
					header="Evidence"
					body={(rowData) => <StringListTemplate list={getRefStrings(rowData.evidence)} />}
				/>
				<Column field="updatedBy.uniqueId" header="Updated By" />
				<Column field="dateUpdated" header="Date Updated" />
			</DataTable>
		</Dialog>
	);
};
