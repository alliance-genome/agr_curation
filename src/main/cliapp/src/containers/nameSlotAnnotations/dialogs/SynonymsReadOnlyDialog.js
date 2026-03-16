import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { evidenceTemplate } from '../../../components/EvidenceComponent';
import {
	synonymScopeTemplate,
	nameTypeTemplate,
	synonymUrlTemplate,
	displayTextTemplate,
	formatTextTemplate,
} from '../../../components/NameSlotAnnotationComponent';
import { EllipsisTableCell } from '../../../components/EllipsisTableCell';

export const SynonymsReadOnlyDialog = ({ originalSynonymsData, setOriginalSynonymsData }) => {
	const { originalSynonyms, isInEdit, dialog } = originalSynonymsData;
	const [localSynonyms, setLocalSynonyms] = useState(null);

	const showDialogHandler = () => {
		let _localSynonyms = [];
		if (originalSynonyms?.length > 0 && originalSynonyms[0]) {
			_localSynonyms = global.structuredClone(originalSynonyms);
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
				<Column field="displayText" header="Display Text" body={displayTextTemplate} />
				<Column field="formatText" header="Format Text" body={formatTextTemplate} />
				<Column field="synonymScope" header="Synonym Scope" body={synonymScopeTemplate} />
				<Column field="nameType" header="Name Type" body={nameTypeTemplate} />
				<Column field="synonymUrl" header="Synonym URL" body={synonymUrlTemplate} />
				<Column field="internal" header="Internal" body={internalTemplate} />
				<Column field="evidence.curie" header="Evidence" body={(rowData) => evidenceTemplate(rowData)} />
				<Column field="updatedBy.uniqueId" header="Updated By" />
				<Column field="dateUpdated" header="Date Updated" />
			</DataTable>
		</Dialog>
	);
};
