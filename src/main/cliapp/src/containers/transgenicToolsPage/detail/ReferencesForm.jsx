import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ReferencesFormTable } from '../../allelesPage/referencesTable/ReferencesFormTable';
import { NewReferenceField } from '../../variantsPage/references/NewReferenceField';

const ENTITY_TYPE = 'references';

/**
 * The transgenic tool detail page's references section. Its rows live in the tool reducer and are saved with
 * the tool.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @returns {JSX.Element}
 */
export const ReferencesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		dispatch({ type: 'DELETE_ROW', entityType: ENTITY_TYPE, dataKey });
	};

	return (
		<FormTableWrapper
			table={
				<ReferencesFormTable
					references={state.transgenicTool?.[ENTITY_TYPE]}
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
