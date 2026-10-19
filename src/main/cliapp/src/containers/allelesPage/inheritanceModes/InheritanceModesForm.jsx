import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { InheritanceModesFormTable } from './InheritanceModesFormTable';
import { useRef } from 'react';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { useAlleleFormStrategy } from '../useAlleleFormStrategy';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';

const ENTITY_TYPE = 'alleleInheritanceModes';

/**
 * The allele detail and create pages' inheritance modes section. Its rows live in the allele
 * reducer and are saved with the allele.
 *
 * @param {object} state - allele reducer state
 * @param {Function} dispatch - allele reducer dispatch
 * @returns {JSX.Element}
 */
export const InheritanceModesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useAlleleFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });
	const inheritanceModeTerms = useControlledVocabularyService('allele_inheritance_mode');

	const createNewInheritanceModeHandler = (e) => {
		e.preventDefault();
		const newInheritanceMode = {
			internal: false,
			obsolete: false,
			inheritanceMode: null,
			phenotypeTerm: null,
			phenotypeStatement: '',
		};

		addDataKey(newInheritanceMode);

		dispatch({
			type: 'ADD_ROW',
			row: newInheritanceMode,
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
			table={
				<TableEditorProvider strategy={strategy}>
					<InheritanceModesFormTable
						inheritanceModes={state.allele?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						inheritanceModeTerms={inheritanceModeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Inheritance Modes"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={
				<Button label="Add Inheritance Mode" onClick={createNewInheritanceModeHandler} className="w-4  p-button-text" />
			}
		/>
	);
};
