import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SynonymsFormTable } from '../../allelesPage/synonyms/SynonymsFormTable';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../allelesPage/utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { useTransgenicToolFormStrategy } from './useTransgenicToolFormStrategy';

const ENTITY_TYPE = 'transgenicToolSynonyms';

/**
 * The transgenic tool detail page's synonyms section. Its rows live in the tool reducer and are saved with
 * the tool.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @returns {JSX.Element}
 */
export const SynonymsForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useTransgenicToolFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');
	// Synonym name types come from the name_type vocabulary, where full name uses a vocabulary term set.
	const nameTypeTerms = useControlledVocabularyService('name_type');

	const createNewSynonymHandler = (event) => {
		event.preventDefault();
		const newSynonym = {
			synonymUrl: '',
			internal: false,
			obsolete: false,
			nameType: null,
			formatText: '',
			displayText: '',
		};
		addDataKey(newSynonym);
		dispatch({ type: 'ADD_ROW', row: newSynonym, entityType: ENTITY_TYPE });
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
					<SynonymsFormTable
						synonyms={state.transgenicTool?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						synonymScopeTerms={synonymScopeTerms}
						nameTypeTerms={nameTypeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Synonyms"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Synonym" onClick={createNewSynonymHandler} className="w-4 p-button-text" />}
		/>
	);
};
