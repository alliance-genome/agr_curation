import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { RelatedNotesFormTable } from '../../allelesPage/relatedNotes/RelatedNotesFormTable';
import { addDataKey } from '../../allelesPage/utils';

const ENTITY_TYPE = 'relatedNotes';

// The note types a transgenic tool takes (VocabularyConstants.CASSETTE_AND_TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET)
const NOTE_TYPE_TERM_SET = 'transgenic_tool_note_type';

/**
 * The transgenic tool detail page's related notes section. Its rows live in the tool reducer and are saved
 * with the tool.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @returns {JSX.Element}
 */
export const RelatedNotesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const relatedNotes = state.transgenicTool?.[ENTITY_TYPE];

	const editRow = (props, field, value) =>
		dispatch({ type: 'EDIT_ROW_BY_KEY', entityType: ENTITY_TYPE, dataKey: props.rowData?.dataKey, field, value });

	const createNewRelatedNoteHandler = (event) => {
		event.preventDefault();
		const newRelatedNote = { internal: false, obsolete: false, noteType: null, freeText: '', references: null };
		addDataKey(newRelatedNote);
		dispatch({ type: 'ADD_ROW', row: newRelatedNote, entityType: ENTITY_TYPE });
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		const updatedErrorMessages = structuredClone(state.entityStates[ENTITY_TYPE].errorMessages);
		delete updatedErrorMessages[dataKey];
		dispatch({ type: 'DELETE_ROW', entityType: ENTITY_TYPE, dataKey });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: ENTITY_TYPE, errorMessages: updatedErrorMessages });
	};

	return (
		<FormTableWrapper
			table={
				<RelatedNotesFormTable
					relatedNotes={relatedNotes}
					editingRows={state.entityStates[ENTITY_TYPE].editingRows}
					onRowEditChange={() => null}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates[ENTITY_TYPE].errorMessages}
					noteTypeVocabType={NOTE_TYPE_TERM_SET}
					// The rows are the reducer's, which freezes them, so the edit goes through dispatch alone:
					// editorCallback would write into the frozen row and throw before the dispatch (SCRUM-6655)
					noteTypeOnChangeHandler={(props, event) => editRow(props, 'noteType', event.target.value)}
					textOnChangeHandler={(rowIndex, event, field) =>
						dispatch({
							type: 'EDIT_ROW_BY_KEY',
							entityType: ENTITY_TYPE,
							dataKey: relatedNotes[rowIndex]?.dataKey,
							field,
							value: event.target.value,
						})
					}
					internalOnChangeHandler={(props, event) => editRow(props, 'internal', event.target?.value?.name)}
					referencesOnChangeHandler={(event, setFieldValue, props) => {
						setFieldValue(event.target.value);
						editRow(props, 'references', event.target.value);
					}}
				/>
			}
			tableName="Related Notes"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />}
		/>
	);
};
