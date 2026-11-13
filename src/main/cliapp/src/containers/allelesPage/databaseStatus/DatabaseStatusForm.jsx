import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { DatabaseStatusFormTable } from './DatabaseStatusFormTable';
import { useRef } from 'react';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';

/**
 * The allele detail and create pages' Database Status section: the allele's one database status as a single-row
 * table. Edits are written to the allele reducer and saved with the allele.
 *
 * @param {string} [labelColumnSize]
 * @param {object} state - useAlleleReducer state
 * @param {Function} dispatch - useAlleleReducer dispatch
 * @param {boolean} [isPending=false] - marks the section as having pending edits
 * @returns {JSX.Element}
 */
export const DatabaseStatusForm = ({ labelColumnSize, state, dispatch, isPending = false }) => {
	const tableRef = useRef(null);
	const entityType = 'alleleDatabaseStatus';
	const databaseStatusTerms = useControlledVocabularyService('allele_db_status');

	const status = state.allele?.[entityType];
	const databaseStatusArray = status ? [status] : [];

	const strategy = useDataKeyRowsStrategy({
		rows: databaseStatusArray,
		errors: state.entityStates[entityType].errorMessages,
		write: ({ field, value }) => dispatch({ type: 'EDIT_OBJECT', entityType, field, value }),
	});

	const createNewDatabaseStatusHandler = (e) => {
		e.preventDefault();
		const newDatabaseStatus = {
			dataKey: 0,
			synonymUrl: '',
			internal: false,
			databaseStatus: null,
		};

		dispatch({
			type: 'ADD_OBJECT',
			value: newDatabaseStatus,
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
					<DatabaseStatusFormTable
						databaseStatuses={databaseStatusArray}
						editingRows={state.entityStates[entityType].editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						databaseStatusTerms={databaseStatusTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Database Status"
			isPending={isPending}
			showTable={state.entityStates[entityType].show}
			button={
				<Button
					label="Add Database Status"
					onClick={createNewDatabaseStatusHandler}
					disabled={state.allele?.[entityType]}
					className="w-4  p-button-text"
				/>
			}
		/>
	);
};
