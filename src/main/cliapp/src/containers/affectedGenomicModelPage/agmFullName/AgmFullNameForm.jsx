import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { AgmFullNameFormTable } from './AgmFullNameFormTable';
import { useRef } from 'react';

export const AgmFullNameForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);

	const fullNameArray = [state.agm?.agmFullName];

	const createNewFullNameHandler = (e) => {
		e.preventDefault();
		const newFullName = {
			dataKey: 0,
			synonymUrl: '',
			internal: false,
			nameType: null,
			formatText: '',
			displayText: '',
		};

		dispatch({
			type: 'ADD_OBJECT',
			value: newFullName,
			entityType: 'agmFullName',
		});
	};

	const onRowEditChange = (e) => {
		return null;
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'agmFullName',
			field: 'nameType',
			value: event.target.value,
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'agmFullName',
			field: 'internal',
			value: event.target?.value?.name,
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'agmFullName',
			field: 'synonymScope',
			value: event.target.value,
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'agmFullName',
			field: field,
			value: event.target.value,
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'agmFullName',
			field: 'evidence',
			value: event.target.value,
		});
	};

	const deletionHandler = (e) => {
		e.preventDefault();
		dispatch({ type: 'DELETE_OBJECT', entityType: 'agmFullName' });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: 'agmFullName', errorMessages: {} });
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<AgmFullNameFormTable
					name={fullNameArray}
					editingRows={state.entityStates.agmFullName.editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates.agmFullName.errorMessages}
					textOnChangeHandler={textOnChangeHandler}
					synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
					nameTypeOnChangeHandler={nameTypeOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
				/>
			}
			tableName="Name"
			showTable={state.entityStates.agmFullName.show}
			button={
				<Button
					label="Add Full Name"
					onClick={createNewFullNameHandler}
					disabled={state.agm?.agmFullName}
					className="w-4 p-button-text"
				/>
			}
		/>
	);
};
