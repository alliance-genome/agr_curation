import React, { useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { InputTextAreaEditor } from '../../components/Editors/text/InputTextAreaEditor';
import { DialogErrorMessageComponent } from '../../components/Error/DialogErrorMessageComponent';
import { BooleanDropdown } from '../../components/Editors/dropdown/boolean/BooleanDropdown';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { ControlledVocabularyDropdown } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyDropdown';
import { FormErrorMessageComponent } from '../../components/Error/FormErrorMessageComponent';

export const RelatedNotesForm = ({ dispatch, relatedNotes, showRelatedNotes, errorMessages, editingRows }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const noteTypeTerms = useVocabularyTermSetService('da_note_type');
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const onRowEditChange = (e) => {
		console.log(e);
	};

	const createNewNoteHandler = (event) => {
		event.preventDefault();

		let count = relatedNotes ? relatedNotes.length : 0;
		dispatch({ type: 'ADD_NEW_NOTE', count });
	};

	const onInternalEditorValueChange = (editorOptions, event) => {
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'relatedNotes',
			field: 'internal',
			index: editorOptions.rowIndex,
			value: event.value.name,
		});
	};

	const internalEditor = (editorOptions) => {
		return (
			<>
				<BooleanDropdown
					options={booleanTerms?.terms || []}
					editorChange={onInternalEditorValueChange}
					editorOptions={editorOptions}
					field={'internal'}
				/>
				<FormErrorMessageComponent errorMessages={errorMessages[editorOptions.rowIndex]} errorField={'internal'} />
			</>
		);
	};

	const onNoteTypeEditorValueChange = (editorOptions, event) => {
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'relatedNotes',
			field: 'noteType',
			index: editorOptions.rowIndex,
			value: event.target.value,
		});
	};

	const noteTypeEditor = (editorOptions) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="noteType"
					options={noteTypeTerms}
					editorChange={onNoteTypeEditorValueChange}
					editorOptions={editorOptions}
					showClear={false}
					dataKey="id"
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[editorOptions.rowIndex]} errorField={'noteType'} />
			</>
		);
	};

	const onFreeTextEditorValueChange = (event, editorOptions) => {
		dispatch({
			type: 'EDIT_ROW',
			tableType: 'relatedNotes',
			field: 'freeText',
			index: editorOptions.rowIndex,
			value: event.target.value,
		});
	};

	const freeTextEditor = (editorOptions, fieldName, errorMessages) => {
		return (
			<>
				<InputTextAreaEditor
					initalValue={editorOptions.value}
					editorChange={(e) => onFreeTextEditorValueChange(e, editorOptions)}
					rows={5}
					columns={30}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[editorOptions.rowIndex]} errorField={fieldName} />
			</>
		);
	};

	const handleDeleteRelatedNote = (event, editorOptions) => {
		event.preventDefault();
		dispatch({
			type: 'DELETE_ROW',
			tableType: 'relatedNotes',
			showType: 'showRelatedNotes',
			index: editorOptions.rowIndex,
		});
	};

	const deleteAction = (editorOptions) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text"
				onClick={(event) => {
					handleDeleteRelatedNote(event, editorOptions);
				}}
			/>
		);
	};

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			{showRelatedNotes && (
				<DataTable
					value={relatedNotes}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					onRowEditChange={onRowEditChange}
					editingRows={editingRows}
					ref={tableRef}
				>
					<Column
						editor={(editorOptions) => deleteAction(editorOptions)}
						body={(editorOptions) => deleteAction(editorOptions)}
						style={{ maxWidth: '4rem' }}
						frozen
						headerClassName="surface-0"
						bodyStyle={{ textAlign: 'center' }}
					/>
					<Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName="surface-0" />
					<Column editor={internalEditor} field="internal" header="Internal" headerClassName="surface-0" />
					<Column
						editor={(editorOptions) => freeTextEditor(editorOptions, 'freeText', errorMessages)}
						field="freeText"
						header="Text"
						headerClassName="surface-0"
					/>
				</DataTable>
			)}
			<div className={`w-2 ${showRelatedNotes ? 'pt-3' : ''} p-field p-col`}>
				<Button label="Add Note" onClick={createNewNoteHandler} />
			</div>
		</div>
	);
};
