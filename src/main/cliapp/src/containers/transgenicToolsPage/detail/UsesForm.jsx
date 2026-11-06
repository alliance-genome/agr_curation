import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useTransgenicToolFormStrategy } from './useTransgenicToolFormStrategy';
import { UsesFormTable } from './UsesFormTable';

/**
 * A detail page's uses section (the transgenic tool's, the cassette's): FBcv terms with their evidence. Its rows
 * live in the page's reducer and are saved with the entity.
 *
 * @param {object} state - the page's reducer state
 * @param {Function} dispatch - the page's reducer dispatch
 * @param {string} [entityKey] - the key the page's entity is held under
 * @param {string} [entityType] - the entity's uses field
 * @param {string} [tableName] - the section's label
 * @returns {JSX.Element}
 */
export const UsesForm = ({
	state,
	dispatch,
	entityKey = 'transgenicTool',
	entityType: ENTITY_TYPE = 'transgenicToolUses',
	tableName = 'Transgenic Tool Uses',
}) => {
	const tableRef = useRef(null);
	const strategy = useTransgenicToolFormStrategy({ state, dispatch, entityType: ENTITY_TYPE, entityKey });

	const createNewUseHandler = (event) => {
		event.preventDefault();
		const newUse = { uses: [], internal: false, obsolete: false, evidence: [] };
		addDataKey(newUse);
		dispatch({ type: 'ADD_ROW', row: newUse, entityType: ENTITY_TYPE });
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
					<UsesFormTable
						uses={state[entityKey]?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName={tableName}
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Use" onClick={createNewUseHandler} className="w-4 p-button-text" />}
		/>
	);
};
