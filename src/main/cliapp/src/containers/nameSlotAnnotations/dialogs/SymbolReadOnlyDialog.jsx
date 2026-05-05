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

export const SymbolReadOnlyDialog = ({ originalSymbolData, setOriginalSymbolData }) => {
	const { originalSymbols, isInEdit, dialog } = originalSymbolData;
	const [localSymbols, setLocalSymbols] = useState(null);

	const showDialogHandler = () => {
		let _localSymbols = [];
		if (originalSymbols?.length > 0 && originalSymbols[0]) {
			_localSymbols = structuredClone(originalSymbols);
			let counter = 0;
			_localSymbols.forEach((sym) => {
				sym.dataKey = counter++;
			});
		}
		setLocalSymbols(_localSymbols);
	};

	const hideDialog = () => {
		setOriginalSymbolData((originalSymbolData) => {
			return {
				...originalSymbolData,
				dialog: false,
			};
		});
		setLocalSymbols([]);
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
			<h3>Symbol</h3>
			<DataTable value={localSymbols} dataKey="dataKey" showGridlines>
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
