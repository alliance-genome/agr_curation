import React, { useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { FormErrorMessageComponent } from "../../components/FormErrorMessageComponent";

export const RelatedNotesForm = ({ dispatch, relatedNotes, showRelatedNotes, errorMessages, editingRows }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const noteTypeTerms = useControlledVocabularyService('Disease annotation note types');
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const onRowEditChange = (e) => {
		console.log(e);
	}

	const createNewNoteHandler = (event) => {
		event.preventDefault();

		let count = relatedNotes ? relatedNotes.length : 0;
		dispatch({type: "ADD_NEW_NOTE", count})
	};

	const onInternalEditorValueChange = (props, event) => {
		dispatch({type: "EDIT_ROW", tableType: "relatedNotes", field: "internal", index: props.rowIndex, value: event.value.name});
	}

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<FormErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const onNoteTypeEditorValueChange = (props, event) => {
		dispatch({type: "EDIT_ROW", tableType: "relatedNotes",  field: "noteType", index: props.rowIndex, value: event.target.value})
	};

	const noteTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="noteType"
					options={noteTypeTerms}
					editorChange={onNoteTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
			</>
		);
	};

	const onFreeTextEditorValueChange = (event, props) => {
		dispatch({type: "EDIT_ROW", tableType: "relatedNotes", field: "freeText", index: props.rowIndex, value: event.target.value})
	};

	const freeTextEditor = (props, fieldName, errorMessages) => {
		return (
			<>
				<InputTextAreaEditor
					initalValue={props.value}
					editorChange={(e) => onFreeTextEditorValueChange(e, props)}
					rows={5}
					columns={30}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
			</>
		);
	};

	const handleDeleteRelatedNote = (event, props) => {
		dispatch({type: "DELETE_ROW", tableType: "relatedNotes", showType: "showRelatedNotes", index: props.rowIndex})
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteRelatedNote(event, props) }}/>
		);
	}

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			{showRelatedNotes &&
				<DataTable value={relatedNotes} dataKey="dataKey" showGridlines editMode='row' 
				onRowEditChange={onRowEditChange} editingRows={editingRows} ref={tableRef}>
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName='surface-0' />
					<Column editor={internalEditor} field="internal" header="Internal" headerClassName='surface-0'/>
					<Column
						editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
						field="freeText"
						header="Text"
						headerClassName='surface-0'
					/>
				</DataTable>
			}
			<div className={`w-2 ${showRelatedNotes ? "pt-3" : ""} p-field p-col`}>
				<Button label="Add Note" onClick={createNewNoteHandler}/>
			</div>
		</div>
	);
};
