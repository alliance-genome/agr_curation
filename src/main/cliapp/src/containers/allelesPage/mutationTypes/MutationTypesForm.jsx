import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { MutationTypesFormTable } from './MutationTypesFormTable';
import { useRef } from 'react';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useAlleleFormStrategy } from '../useAlleleFormStrategy';

const ENTITY_TYPE = 'alleleMutationTypes';

/**
 * The allele detail and create pages' mutation types section. Its rows live in the allele
 * reducer and are saved with the allele.
 *
 * @param {object} state - allele reducer state
 * @param {Function} dispatch - allele reducer dispatch
 * @returns {JSX.Element}
 */
export const MutationTypesForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useAlleleFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });

	const createNewMutationTypeHandler = (e) => {
		e.preventDefault();
		const newMutationType = {
			internal: false,
		};

		addDataKey(newMutationType);

		dispatch({
			type: 'ADD_ROW',
			row: newMutationType,
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
					<MutationTypesFormTable
						mutationTypes={state.allele?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Mutation Types"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={
				<Button label="Add Mutation Type" onClick={createNewMutationTypeHandler} className="w-4  p-button-text" />
			}
		/>
	);
};
