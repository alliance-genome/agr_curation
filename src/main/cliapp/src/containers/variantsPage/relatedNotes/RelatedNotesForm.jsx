import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { RelatedNotesFormTable } from '../../../components/relatedNotes/RelatedNotesFormTable';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';

const ENTITY_TYPE = 'relatedNotes';

// The note types a variant takes
const NOTE_TYPE_TERM_SET = 'variant_note_type';

/**
 * The variant detail and create pages' related notes section. Its rows live in the variant reducer and are
 * saved with the variant.
 *
 * @param {object} state - useVariantReducer state
 * @param {Function} dispatch - useVariantReducer dispatch
 * @returns {JSX.Element}
 */
export const RelatedNotesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const relatedNotes = state.variant?.[ENTITY_TYPE];
	const entityState = state.entityStates[ENTITY_TYPE];
	const noteTypeTerms = useVocabularyTermSetService(NOTE_TYPE_TERM_SET);

	const strategy = useDataKeyRowsStrategy({
		rows: relatedNotes,
		errors: entityState.errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW_BY_KEY', entityType: ENTITY_TYPE, dataKey: rowKey, field, value }),
	});

	const createNewRelatedNoteHandler = (event) => {
		event.preventDefault();
		const newRelatedNote = { internal: false, obsolete: false, noteType: null, freeText: '', references: null };
		addDataKey(newRelatedNote);
		dispatch({ type: 'ADD_ROW', row: newRelatedNote, entityType: ENTITY_TYPE });
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		const updatedErrorMessages = structuredClone(entityState.errorMessages);
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
						editingRows={entityState.editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						noteTypeTerms={noteTypeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Related Notes"
			showTable={entityState.show}
			button={<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />}
		/>
	);
};
