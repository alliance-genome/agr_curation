import React, { useRef } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { synonymScopeTemplate, nameTypeTemplate, synonymUrlTemplate, displayTextTemplate, formatTextTemplate } from '../../components/NameSlotAnnotationComponent';

export const SynonymsDialog = ({originalSynonymsData, setOriginalSynonymsData}) => {
	const { originalSynonyms, dialog } = originalSynonymsData;
	const tableRef = useRef(null);
	
	const hideDialog = () => {
		setOriginalSynonymsData((originalSynonymsData) => {
			return {
				...originalSynonymsData,
				dialog: false,
			};
		});
	};
	
	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Display Text" />
					<Column header="Format Text" />
					<Column header="Synonym Scope" />
					<Column header="Name Type" />
					<Column header="Synonym URL" />
					<Column header="Internal" />
					<Column header="Evidence" />
					<Column header="Updated By" />
					<Column header="Date Updated" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Dialog visible={dialog} className='w-10' modal onHide={hideDialog} closable={true} resizable>
				<h3>Synonyms</h3>
				<DataTable value={originalSynonyms} dataKey="dataKey" showGridlines headerColumnGroup={headerGroup} ref={tableRef}>
					<Column field="displayText" header="Display Text" headerClassName='surface-0' body={displayTextTemplate}/>
					<Column field="formatText" header="Format Text" headerClassName='surface-0' body={formatTextTemplate}/>
					<Column field="synonymScope" header="Synonym Scope" headerClassName='surface-0' body={synonymScopeTemplate}/>
					<Column field="nameType" header="Name Type" headerClassName='surface-0' body={nameTypeTemplate}/>
					<Column field="synonymUrl" header="Synonym URL" headerClassName='surface-0' body={synonymUrlTemplate}/>
					<Column field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
					<Column field="updatedBy.uniqueId" header="Updated By" />
					<Column field="dateUpdated" header="Date Updated" />
				</DataTable>
			</Dialog>
		</div>
	);
};
