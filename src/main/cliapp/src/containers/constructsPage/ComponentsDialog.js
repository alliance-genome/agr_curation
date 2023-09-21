import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tooltip } from 'primereact/tooltip';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';

export const ComponentsDialog = ({
	originalComponentsData,
	setOriginalComponentsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow
}) => {
	const { originalComponents, dialog } = originalComponentsData;
	const [localComponents, setLocalComponents] = useState(null) ;
	const tableRef = useRef(null);

	const showDialogHandler = () => {
		let _localComponents = cloneComponents(originalComponents);
		setLocalComponents(_localComponents);
	};

	const hideDialog = () => {
		setOriginalComponentsData((originalComponentsData) => {
			return {
				...originalComponentsData,
				dialog: false,
			};
		});
		let _localComponents = [];
		setLocalComponents(_localComponents);
	};

	const cloneComponents = (clonableComponents) => {
		let _clonableComponents = global.structuredClone(clonableComponents);
		if(_clonableComponents) {
			let counter = 0 ;
			_clonableComponents.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableComponents = [];
		};
		return _clonableComponents;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return (
				<div>
					{`Notes(${rowData.relatedNotes.length})`}
				</div>
			)
		}
	};

	const componentSymbolTemplate = (rowData) => {
		return (
			<>
				<div className={`overflow-hidden text-overflow-ellipsis ${rowData.id}`} dangerouslySetInnerHTML={{ __html: rowData.componentSymbol }} />
				<Tooltip target={`.componentSymbol_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.componentSymbol }} />
				</Tooltip>
			</>
		)
	}

	const taxonTextTemplate = (rowData) => {
		return (
			<>
				<div className={`overflow-hidden text-overflow-ellipsis ${rowData.id}`} dangerouslySetInnerHTML={{ __html: rowData.taxonText }} />
				<Tooltip target={`.taxonText_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.taxonText }} />
				</Tooltip>
			</>
		)
	}

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	let headerGroup = 	<ColumnGroup>
							<Row>
								<Column header="Component Symbol" />
								<Column header="Taxon" />
								<Column header="Taxon Text" />
								<Column header="Internal" />
								<Column header="Evidence" />
								<Column header="RelatedNotes" />
							</Row>
						</ColumnGroup>;

	return (
		<>
			<div>
				<Dialog visible={dialog} className='w-8' modal onHide={hideDialog} closable={true} onShow={showDialogHandler} >
					<h3>Components</h3>
					<DataTable value={localComponents} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
							ref={tableRef} >
						<Column field="componentSymbol" header="Mutation Type" headerClassName='surface-0' body={componentSymbolTemplate}/>
						<Column field="taxon" header="Taxon" headerClassName='surface-0' body={taxonTemplate}/>
						<Column field="taxonText" header="Taxon Text" headerClassName='surface-0' body={taxonTextTemplate}/>
						<Column field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
						<Column field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
						<Column field="relatedNotes.freeText" header="Related Notes" headerClassName='surface-0' body={relatedNotesTemplate}/>
					</DataTable>
				</Dialog>
			</div>
		</>
	);
};
