import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { GermlineTransmissionStatusFormTable } from './GermlineTransmissionStatusFormTable';
import { useRef } from 'react';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';

/**
 * The allele detail and create pages' Germline Transmission Status section: the allele's one germline transmission
 * status as a single-row table. Edits are written to the allele reducer and saved with the allele.
 *
 * @param {string} [labelColumnSize]
 * @param {object} state - useAlleleReducer state
 * @param {Function} dispatch - useAlleleReducer dispatch
 * @param {boolean} [isPending=false] - marks the section as having pending edits
 * @returns {JSX.Element}
 */
export const GermlineTransmissionStatusForm = ({ labelColumnSize, state, dispatch, isPending = false }) => {
	const tableRef = useRef(null);
	const entityType = 'alleleGermlineTransmissionStatus';
	const germlineTransmissionStatusTerms = useControlledVocabularyService('allele_germline_transmission_status');

	const status = state.allele?.[entityType];
	const germlineTransmissionStatusArray = status ? [status] : [];

	const strategy = useDataKeyRowsStrategy({
		rows: germlineTransmissionStatusArray,
		errors: state.entityStates[entityType].errorMessages,
		write: ({ field, value }) => dispatch({ type: 'EDIT_OBJECT', entityType, field, value }),
	});

	const createNewGermlineTransmissionStatusHandler = (e) => {
		e.preventDefault();
		const newGermlineTransmissionStatus = {
			dataKey: 0,
			internal: false,
		};

		dispatch({
			type: 'ADD_OBJECT',
			value: newGermlineTransmissionStatus,
			entityType: entityType,
		});
	};

	const deletionHandler = (e) => {
		e.preventDefault();
		dispatch({ type: 'DELETE_OBJECT', entityType: entityType });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: entityType, errorMessages: {} });
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<TableEditorProvider strategy={strategy}>
					<GermlineTransmissionStatusFormTable
						germlineTransmissionStatuses={germlineTransmissionStatusArray}
						editingRows={state.entityStates[entityType].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						germlineTransmissionStatusTerms={germlineTransmissionStatusTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Germline Transmission Status"
			isPending={isPending}
			showTable={state.entityStates[entityType].show}
			button={
				<Button
					label="Add Germline Transmission Status"
					onClick={createNewGermlineTransmissionStatusHandler}
					disabled={state.allele?.[entityType]}
					className="w-4  p-button-text"
				/>
			}
		/>
	);
};
