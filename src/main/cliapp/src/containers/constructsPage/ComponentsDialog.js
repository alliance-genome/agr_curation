import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Tooltip } from 'primereact/tooltip';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { evidenceTemplate } from '../../components/EvidenceComponent';
import { Button } from 'primereact/button';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';

export const ComponentsDialog = ({
	originalComponentsData,
	setOriginalComponentsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalComponents, dialog } = originalComponentsData;
	const [localComponents, setLocalComponents] = useState(null);
	const tableRef = useRef(null);

	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

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
		if (_clonableComponents) {
			let counter = 0;
			_clonableComponents.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableComponents = [];
		}
		return _clonableComponents;
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const handleRelatedNotesOpen = (event, rowData, isInEdit) => {
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = rowData.relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = isInEdit;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return (
				<Button
					className="p-button-text"
					onClick={(event) => {
						handleRelatedNotesOpen(event, rowData, false);
					}}
				>
					<span style={{ textDecoration: 'underline' }}>{`Notes(${rowData.relatedNotes.length})`}</span>
				</Button>
			);
		}
	};

	const componentSymbolTemplate = (rowData) => {
		return (
			<>
				<div
					className={`overflow-hidden text-overflow-ellipsis componentSymbol_${rowData.id}`}
					dangerouslySetInnerHTML={{ __html: rowData.componentSymbol }}
				/>
				<Tooltip target={`.componentSymbol_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.componentSymbol }} />
				</Tooltip>
			</>
		);
	};

	const taxonTextTemplate = (rowData) => {
		return (
			<>
				<div
					className={`overflow-hidden text-overflow-ellipsis taxonText_${rowData.id}`}
					dangerouslySetInnerHTML={{ __html: rowData.taxonText }}
				/>
				<Tooltip target={`.taxonText_${rowData.id}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.taxonText }} />
				</Tooltip>
			</>
		);
	};

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${'TAXON_NAME_'}${rowData.id}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip
						target={`.${'TAXON_NAME_'}${rowData.id}${rowData.taxon.curie.replace(':', '')}`}
						content={`${rowData.taxon.name} (${rowData.taxon.curie})`}
						style={{ width: '250px', maxWidth: '450px' }}
					/>
				</>
			);
		}
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Relation" />
				<Column header="Component Symbol" />
				<Column header="Taxon" />
				<Column header="Taxon Text" />
				<Column header="RelatedNotes" />
				<Column header="Evidence" />
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

	return (
		<>
			<div>
				<Dialog visible={dialog} className="w-8" modal onHide={hideDialog} closable={true} onShow={showDialogHandler}>
					<h3>Components</h3>
					<DataTable
						value={localComponents}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						headerColumnGroup={headerGroup}
						ref={tableRef}
					>
						<Column field="relation.name" header="Relation" headerClassName="surface-0" />
						<Column
							field="componentSymbol"
							header="Component Symbol"
							headerClassName="surface-0"
							body={componentSymbolTemplate}
						/>
						<Column field="taxon.name" header="Taxon" headerClassName="surface-0" body={taxonTemplate} />
						<Column field="taxonText" header="Taxon Text" headerClassName="surface-0" body={taxonTextTemplate} />
						<Column
							field="relatedNotes.freeText"
							header="Related Notes"
							headerClassName="surface-0"
							body={relatedNotesTemplate}
						/>
						<Column
							field="evidence.curie"
							header="Evidence"
							headerClassName="surface-0"
							body={(rowData) => evidenceTemplate(rowData)}
						/>
						<Column field="internal" header="Internal" body={internalTemplate} headerClassName="surface-0" />
					</DataTable>
				</Dialog>
			</div>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessagesMainRow}
				setErrorMessagesMainRow={setErrorMessagesMainRow}
				noteTypeVocabularyTermSet="construct_component_note_type"
			/>
		</>
	);
};
