import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { RelatedNotesFormTable } from '../relatedNotes/RelatedNotesFormTable';
import { useRef } from 'react';
import { addDataKey } from '../utils';

export const RelatedNotesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const entityType = 'relatedNotes';
	const relatedNotes = global.structuredClone(state.allele?.[entityType]);

	const createNewRelatedNoteHandler = (e) => {
		e.preventDefault();
		const newRelatedNote = {
			internal: false,
			obsolete: false,
			noteType: null,
			freeText: '',
			references: null,
		};

		addDataKey(newRelatedNote);

		dispatch({
			type: 'ADD_ROW',
			row: newRelatedNote,
			entityType: entityType,
		});
	};

	const onRowEditChange = (e) => {
		return null;
	};

	const noteTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'noteType',
			value: event.target.value,
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'internal',
			value: event.target?.value?.name,
		});
	};

	const referencesOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'references',
			value: event.target.value,
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: rowIndex,
			field: field,
			value: event.target.value,
		});
	};

	const deletionHandler = (e, dataKey) => {
		e.preventDefault();
		const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
		delete updatedErrorMessages[dataKey];
		dispatch({ type: 'DELETE_ROW', entityType: entityType, dataKey });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: entityType, errorMessages: updatedErrorMessages });
	};

	return (
		<FormTableWrapper
			table={
				<RelatedNotesFormTable
					relatedNotes={relatedNotes}
					editingRows={state.entityStates[entityType].editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates[entityType].errorMessages}
					noteTypeOnChangeHandler={noteTypeOnChangeHandler}
					textOnChangeHandler={textOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					referencesOnChangeHandler={referencesOnChangeHandler}
				/>
			}
			tableName="Related Notes"
			showTable={state.entityStates[entityType].show}
			button={<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />}
		/>
	);
};
