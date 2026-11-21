import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';
import { getRefStrings } from '../../../utils/utils';
import { StringTemplate } from '../../../components/Templates/StringTemplate';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';

export const SynonymsReadOnlyDialog = ({ originalSynonymsData, setOriginalSynonymsData }) => {
	const { originalSynonyms, isInEdit, dialog } = originalSynonymsData;
	const [localSynonyms, setLocalSynonyms] = useState(null);

	const showDialogHandler = () => {
		let _localSynonyms = [];
		if (originalSynonyms?.length > 0 && originalSynonyms[0]) {
			_localSynonyms = structuredClone(originalSynonyms);
			let counter = 0;
			_localSynonyms.forEach((syn) => {
				syn.dataKey = counter++;
			});
		}
		setLocalSynonyms(_localSynonyms);
	};

	const hideDialog = () => {
		setOriginalSynonymsData((originalSynonymsData) => {
			return {
				...originalSynonymsData,
				dialog: false,
			};
		});
		setLocalSynonyms([]);
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
			<h3>Synonyms</h3>
			<DataTable value={localSynonyms} dataKey="dataKey" showGridlines>
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
