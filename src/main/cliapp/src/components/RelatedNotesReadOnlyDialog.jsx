import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from './EllipsisTableCell';
import { ListTableCell } from './ListTableCell';
import { getRefStrings } from '../utils/utils';

export const RelatedNotesReadOnlyDialog = ({
	originalRelatedNotesData,
	setOriginalRelatedNotesData,
	showReferences = true,
}) => {
	const { originalRelatedNotes, isInEdit, dialog } = originalRelatedNotesData;
	const [localRelatedNotes, setLocalRelatedNotes] = useState(null);

	const showDialogHandler = () => {
		let _localRelatedNotes = [];
		if (originalRelatedNotes) {
			_localRelatedNotes = structuredClone(originalRelatedNotes);
			let counter = 0;
			_localRelatedNotes.forEach((note) => {
				note.dataKey = counter++;
			});
		}
		setLocalRelatedNotes(_localRelatedNotes);
	};

	const hideDialog = () => {
		setOriginalRelatedNotesData((originalRelatedNotesData) => {
			return {
				...originalRelatedNotesData,
				dialog: false,
			};
		});
		setLocalRelatedNotes([]);
	};

	const noteTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.noteType?.name}</EllipsisTableCell>;
	};

	const textTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
	};

	const evidenceTemplate = (rowData) => {
		if (rowData && rowData.references) {
			const refStrings = getRefStrings(rowData.references);
			const listTemplate = (item) => {
				return <EllipsisTableCell>{item}</EllipsisTableCell>;
			};
			return <ListTableCell template={listTemplate} listData={refStrings} />;
		}
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	return (
		<Dialog visible={dialog && !isInEdit} className="w-8" modal onHide={hideDialog} closable onShow={showDialogHandler}>
			<h3>Related Notes</h3>
			<DataTable value={localRelatedNotes} dataKey="dataKey" showGridlines>
				<Column field="noteType.name" header="Note Type" body={noteTypeTemplate} />
				<Column field="freeText" header="Text" body={textTemplate} className="wrap-word max-w-35rem" />
				{showReferences && (
					<Column field="evidence.curie" header="Evidence" body={evidenceTemplate} className="wrap-word max-w-25rem" />
				)}
				<Column field="internal" header="Internal" body={internalTemplate} />
			</DataTable>
		</Dialog>
	);
};
