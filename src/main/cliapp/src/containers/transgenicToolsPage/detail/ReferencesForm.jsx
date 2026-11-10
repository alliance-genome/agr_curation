import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ReferencesFormTable } from '../../allelesPage/referencesTable/ReferencesFormTable';
import { NewReferenceField } from '../../variantsPage/references/NewReferenceField';

const ENTITY_TYPE = 'references';

/**
 * A detail page's references section (the transgenic tool's, the cassette's). Its rows live in the page's
 * reducer and are saved with the entity.
 *
 * @param {object} state - the page's reducer state
 * @param {Function} dispatch - the page's reducer dispatch
 * @param {string} [entityKey] - the key the page's entity is held under
 * @returns {JSX.Element}
 */
export const ReferencesForm = ({ state, dispatch, entityKey = 'transgenicTool' }) => {
	const tableRef = useRef(null);

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		dispatch({ type: 'DELETE_ROW', entityType: ENTITY_TYPE, dataKey });
	};

	return (
		<FormTableWrapper
			table={
				<ReferencesFormTable
					references={state[entityKey]?.[ENTITY_TYPE]}
					editingRows={state.entityStates[ENTITY_TYPE].editingRows}
					errorMessages={state.entityStates[ENTITY_TYPE].errorMessages}
					onRowEditChange={() => null}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
				/>
			}
			tableName="References"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={<NewReferenceField state={state} dispatch={dispatch} />}
			includeField={true}
		/>
	);
};
