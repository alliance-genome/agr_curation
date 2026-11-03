import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SecondaryIdsFormTable } from '../../allelesPage/secondaryIds/SecondaryIdsFormTable';
import { useRef } from 'react';
import { addDataKey } from '../utils';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../allelesPage/utils';
import { useAGMFormStrategy } from '../useAGMFormStrategy';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';

const ENTITY_TYPE = 'agmSecondaryIds';

/**
 * The AGM detail and create pages' secondary IDs section. Its rows live in the AGM reducer
 * and are saved with the AGM.
 *
 * @param {object} state - AGM reducer state
 * @param {Function} dispatch - AGM reducer dispatch
 * @returns {JSX.Element}
 */
export const SecondaryIdsForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useAGMFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });

	const createNewSecondaryIdHandler = (e) => {
		e.preventDefault();

		const newSecondaryId = {
			internal: false,
			obsolete: false,
			secondaryId: '',
		};

		addDataKey(newSecondaryId);

		dispatch({
			type: 'ADD_ROW',
			row: newSecondaryId,
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
					<SecondaryIdsFormTable
						secondaryIds={state.agm?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Secondary IDs"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<Button label="Add Secondary ID" onClick={createNewSecondaryIdHandler} className="w-4 p-button-text" />}
		/>
	);
};
