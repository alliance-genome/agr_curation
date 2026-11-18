import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { RelatedNotesFormTable } from '../../../components/relatedNotes/RelatedNotesFormTable';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { useTransgenicToolFormStrategy } from './useTransgenicToolFormStrategy';

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

	const noteTypeTerms = useVocabularyTermSetService(NOTE_TYPE_TERM_SET);
	const strategy = useTransgenicToolFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });

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
				<TableEditorProvider strategy={strategy}>
					<RelatedNotesFormTable
						relatedNotes={relatedNotes}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						noteTypeTerms={noteTypeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Related Notes"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />}
		/>
	);
};
