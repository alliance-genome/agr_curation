import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { CrossReferencesTable } from '../../allelesPage/crossReferences/CrossReferencesTable';
import { applyCrossReferenceFieldChange, buildNewCrossReference } from '../../allelesPage/crossReferences/utils';

const ENTITY_TYPE = 'crossReferences';

/**
 * The transgenic tool detail page's cross references section. Unlike an allele's, a tool's cross references
 * are saved with the tool, so its rows live in the tool reducer. Each row carries the descriptor it was seeded
 * with (seedResourceDescriptors), which the payload drops.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @returns {JSX.Element}
 */
export const CrossReferencesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const crossReferences = state.transgenicTool?.[ENTITY_TYPE] ?? [];

	const onFieldChange = (dataKey, field, value) => {
		const crossReference = crossReferences.find((row) => row.dataKey === dataKey);
		if (!crossReference) return;
		dispatch({
			type: 'REPLACE_ROW',
			entityType: ENTITY_TYPE,
			row: applyCrossReferenceFieldChange(crossReference, field, value),
		});
	};

	const createNewCrossReferenceHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_ROW', row: buildNewCrossReference(), entityType: ENTITY_TYPE, append: true });
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
				<CrossReferencesTable
					crossReferences={crossReferences}
					editingRows={state.entityStates[ENTITY_TYPE].editingRows}
					onRowEditChange={() => null}
					tableRef={tableRef}
					errorMessages={state.entityStates[ENTITY_TYPE].errorMessages}
					deletionHandler={deletionHandler}
					onFieldChange={onFieldChange}
					showObsolete={true}
				/>
			}
			tableName="Cross References"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={
				<Button label="Add Cross Reference" onClick={createNewCrossReferenceHandler} className="w-4 p-button-text" />
			}
		/>
	);
};
