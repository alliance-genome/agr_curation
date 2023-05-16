import React, { useRef } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';

export const SecondaryIdsDialog = ({originalSecondaryIdsData, setOriginalSecondaryIdsData}) => {
	const { originalSecondaryIds, dialog } = originalSecondaryIdsData;
	const tableRef = useRef(null);
	
	const hideDialog = () => {
		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
				dialog: false,
			};
		});
	};
	
	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const secondaryIdTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.secondaryId}</EllipsisTableCell>;
	};

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Secondary ID" />
					<Column header="Internal" />
					<Column header="Evidence" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={true} resizable>
				<h3>Secondary IDs</h3>
				<DataTable value={originalSecondaryIds} dataKey="dataKey" showGridlines headerColumnGroup={headerGroup}
								ref={tableRef}>
					<Column field="secondaryId" header="Secondary ID" headerClassName='surface-0' body={secondaryIdTemplate}/>
					<Column field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
