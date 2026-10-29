import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { FormErrorMessageComponent } from '../../../components/Error/FormErrorMessageComponent';
import { CrossReferencesTable } from '../../allelesPage/crossReferences/CrossReferencesTable';
import { applyCrossReferenceFieldChange, buildNewCrossReference } from '../../allelesPage/crossReferences/utils';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../allelesPage/utils';

const ENTITY_TYPE = 'gcrpCrossReference';

/**
 * The gene detail page's GCRP cross reference section: the gene's one GCRP cross reference, held in the gene
 * reducer and saved with the gene. A gene has at most one, so it can be added only while there is none. The API
 * reports what is wrong with it as one message for the field, shown beside the Add button.
 *
 * @param {Object} props
 * @param {Object} props.state - useGeneReducer state
 * @param {Function} props.dispatch - useGeneReducer dispatch
 * @param {boolean} [props.isPending] - whether to mark the section as having pending edits
 */
export const GcrpCrossReferenceForm = ({ state, dispatch, isPending = false }) => {
	const tableRef = useRef(null);
	const gcrpCrossReference = state.gene?.gcrpCrossReference;
	const entityState = state.entityStates[ENTITY_TYPE];

	const onFieldChange = (dataKey, field, value) => {
		dispatch({
			type: 'UPDATE_OBJECT',
			entityType: ENTITY_TYPE,
			update: (crossReference) => applyCrossReferenceFieldChange(crossReference, field, value),
		});
	};

	const addHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_OBJECT', entityType: ENTITY_TYPE, value: buildNewCrossReference() });
	};

	const deletionHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'DELETE_OBJECT', entityType: ENTITY_TYPE });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: ENTITY_TYPE, errorMessages: {} });
	};

	return (
		<FormTableWrapper
			table={
				<CrossReferencesTable
					crossReferences={gcrpCrossReference ? [gcrpCrossReference] : []}
					editingRows={entityState.editingRows}
					onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
					tableRef={tableRef}
					errorMessages={entityState.errorMessages}
					deletionHandler={deletionHandler}
					onFieldChange={onFieldChange}
					showObsolete
				/>
			}
			tableName="GCRP Cross Reference"
			showTable={entityState.show}
			isPending={isPending}
			button={
				<>
					<Button
						label="Add GCRP Cross Reference"
						onClick={addHandler}
						disabled={!!gcrpCrossReference}
						className="w-4 p-button-text"
					/>
					<FormErrorMessageComponent errorMessages={state.errorMessages} errorField={ENTITY_TYPE} />
				</>
			}
		/>
	);
};
