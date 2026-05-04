import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useRef } from 'react';
import { GermlineTransmissionStatusFormTable } from './GermlineTransmissionStatusFormTable';

export const GermilineTransmissionStatusForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);
	const entityType = 'alleleGermlineTransmissionStatus';
	const germlineTransmissionStatusArray = [state.allele?.[entityType]];

	const createNewGermlineTransmissionStatus = (e) => {
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

	const onRowEditChange = (e) => {
		return null;
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

	const germlineTransmissionStatusOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_OBJECT',
			entityType: entityType,
			field: 'germlineTransmissionStatus',
			value: event.target.value,
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
				<GermlineTransmissionStatusFormTable
					name={germlineTransmissionStatusArray}
					editingRows={state.entityStates[entityType].editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates[entityType].errorMessages}
					germlineTransmissionStatusOnChangeHandler={germlineTransmissionStatusOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
				/>
			}
			tableName="Germline Transmission Status"
			showTable={state.entityStates[entityType].show}
			button={
				<Button
					label="Add Germline Transmission Status"
					onClick={createNewGermlineTransmissionStatus}
					disabled={state.allele?.[entityType]}
					className="w-4  p-button-text"
				/>
			}
		/>
	);
};
