import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useRef } from 'react';
import { FunctionalImpactsFormTable } from './FunctionalImpactsFormTable';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { useAlleleFormStrategy } from '../useAlleleFormStrategy';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';

const ENTITY_TYPE = 'alleleFunctionalImpacts';

/**
 * The allele detail and create pages' functional impacts section. Its rows live in the allele
 * reducer and are saved with the allele.
 *
 * @param {object} state - allele reducer state
 * @param {Function} dispatch - allele reducer dispatch
 * @returns {JSX.Element}
 */
export const FunctionalImpactsForm = ({ state, dispatch }) => {
	const tableRef = useRef(null);
	const strategy = useAlleleFormStrategy({ state, dispatch, entityType: ENTITY_TYPE });

	const createNewFunctionalImpactsHandler = (e) => {
		e.preventDefault();
		const newFunctionalImpact = {
			phenotypeStatement: '',
			internal: false,
		};

		addDataKey(newFunctionalImpact);

		dispatch({
			type: 'ADD_ROW',
			row: newFunctionalImpact,
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
					<FunctionalImpactsFormTable
						functionalImpacts={state.allele?.[ENTITY_TYPE]}
						editingRows={state.entityStates[ENTITY_TYPE].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Functional Impacts"
			showTable={state.entityStates[ENTITY_TYPE].show}
			button={
				<Button
					label="Add Functional Impact"
					onClick={createNewFunctionalImpactsHandler}
					className="w-4 p-button-text"
				/>
			}
		/>
	);
};
