import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { FullNameFormTable } from './FullNameFormTable';
import { useRef } from 'react';

export const FullNameForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);

	const fullNameArray = [state.allele?.alleleFullName];

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
			entityType: 'alleleFullName',
		});
	};

	const onRowEditChange = (e) => {
		return null;
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'alleleFullName',
			field: 'nameType',
			value: event.target.value,
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'alleleFullName',
			field: 'internal',
			value: event.target?.value?.name,
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'alleleFullName',
			field: 'synonymScope',
			value: event.target.value,
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'alleleFullName',
			field: field,
			value: event.target.value,
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: 'alleleFullName',
			field: 'evidence',
			value: event.target.value,
		});
	};

	const deletionHandler = (e) => {
		e.preventDefault();
		dispatch({ type: 'DELETE_OBJECT', entityType: 'alleleFullName' });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: 'alleleFullName', errorMessages: {} });
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<FullNameFormTable
					name={fullNameArray}
					editingRows={state.entityStates.alleleFullName.editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates.alleleFullName.errorMessages}
					textOnChangeHandler={textOnChangeHandler}
					synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
					nameTypeOnChangeHandler={nameTypeOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
				/>
			}
			tableName="Full Name"
			showTable={state.entityStates.alleleFullName.show}
			button={
				<Button
					label="Add Full Name"
					onClick={createNewFullNameHandler}
					disabled={state.allele?.alleleFullName}
					className="w-4 p-button-text"
				/>
			}
		/>
	);
};
