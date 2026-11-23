import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StringListTemplate } from '../../../components/Templates/StringListTemplate';
import { getRefStrings } from '../../../utils/utils';
import { StringTemplate } from '../../../components/Templates/StringTemplate';
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
