import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { DatabaseStatusFormTable } from './DatabaseStatusFormTable';
import { useRef } from 'react';

export const DatabaseStatusForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);
	const entityType = 'alleleDatabaseStatus';

	const databaseStatusArray = [state.allele?.[entityType]];

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

	const onRowEditChange = (e) => {
		return null;
	};

	const databaseStatusOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: entityType,
			field: 'databaseStatus',
			value: event.target.value,
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: entityType,
			field: 'internal',
			value: event.target?.value?.name,
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: entityType,
			field: 'evidence',
			value: event.target.value,
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
				<DatabaseStatusFormTable
					databaseStatuses={databaseStatusArray}
					editingRows={state.entityStates[entityType].editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates[entityType].errorMessages}
					databaseStatusOnChangeHandler={databaseStatusOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
				/>
			}
			tableName="Database Status"
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
