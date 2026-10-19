import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SynonymsFormTable } from './SynonymsFormTable';
import { useRef } from 'react';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { useAlleleFormStrategy } from '../useAlleleFormStrategy';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';

const ENTITY_TYPE = 'alleleSynonyms';

/**
 * The allele detail and create pages' synonyms section. Its rows live in the allele reducer
 * and are saved with the allele.
 *
 * @param {string} [labelColumnSize] - grid class for the section label column
 * @param {object} state - allele reducer state
 * @param {Function} dispatch - allele reducer dispatch
 * @returns {JSX.Element}
 */
export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useAlleleFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');
	// Synonym name types come from the name_type vocabulary, where symbol and full name use a
	// vocabulary term set.
	const nameTypeTerms = useControlledVocabularyService('name_type');

	const createNewSynonymHandler = (e) => {
		e.preventDefault();
		const newSynonym = {
			synonymUrl: '',
			internal: false,
			obsolete: false,
			nameType: null,
			formatText: '',
			displayText: '',
		};

		addDataKey(newSynonym);

		dispatch({
			type: 'ADD_ROW',
			row: newSynonym,
			entityType: ENTITY_TYPE,
		});
	};

	const deletionHandler = (e, dataKey) => {
		e.preventDefault();
		const updatedErrorMessages = structuredClone(state.entityStates[ENTITY_TYPE].errorMessages);
		delete updatedErrorMessages[dataKey];
		dispatch({ type: 'DELETE_ROW', entityType: ENTITY_TYPE, dataKey });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: ENTITY_TYPE, errorMessages: updatedErrorMessages });
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<TableEditorProvider strategy={strategy}>
					<SynonymsFormTable
						synonyms={state.allele?.[ENTITY_TYPE]}
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
