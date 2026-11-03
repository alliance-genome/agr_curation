import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../allelesPage/utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useTransgenicToolFormStrategy } from './useTransgenicToolFormStrategy';
import { UsesFormTable } from './UsesFormTable';

const ENTITY_TYPE = 'transgenicToolUses';

/**
 * The transgenic tool detail page's uses section. Its rows live in the tool reducer and are saved with the
 * tool.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @returns {JSX.Element}
 */
export const UsesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useTransgenicToolFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });

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
						uses={state.transgenicTool?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Transgenic Tool Uses"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Use" onClick={createNewUseHandler} className="w-4 p-button-text" />}
		/>
	);
};
