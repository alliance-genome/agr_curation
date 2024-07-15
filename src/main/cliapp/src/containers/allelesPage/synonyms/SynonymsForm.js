import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SynonymsFormTable } from './SynonymsFormTable';
import { useRef } from 'react';
import { addDataKey } from '../utils';

export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
	const tableRef = useRef(null);
	const entityType = 'alleleSynonyms';
	const synonyms = global.structuredClone(state.allele?.[entityType]);

	const createNewSynonymHandler = (e) => {
		e.preventDefault();
		const newSynonym = {
			synonymUrl: '',
			internal: false,
			obsolete: false,
			nameType: null,
			formatText: '',
			displayText: '',
		};

		addDataKey(newSynonym);

		dispatch({
			type: 'ADD_ROW',
			row: newSynonym,
			entityType: entityType,
		});
	};

	const onRowEditChange = (e) => {
		return null;
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'nameType',
			value: event.target.value,
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'internal',
			value: event.target?.value?.name,
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'synonymScope',
			value: event.target.value,
		});
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: rowIndex,
			field: field,
			value: event.target.value,
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		dispatch({
			type: 'EDIT_ROW',
			entityType: entityType,
			index: props.rowIndex,
			field: 'evidence',
			value: event.target.value,
		});
	};

	const deletionHandler = (e, dataKey) => {
		e.preventDefault();
		const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
		delete updatedErrorMessages[dataKey];
		dispatch({ type: 'DELETE_ROW', entityType: entityType, dataKey });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: entityType, errorMessages: updatedErrorMessages });
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<SynonymsFormTable
					synonyms={synonyms}
					editingRows={state.entityStates[entityType].editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={state.entityStates[entityType].errorMessages}
					textOnChangeHandler={textOnChangeHandler}
					synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
					nameTypeOnChangeHandler={nameTypeOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
				/>
			}
			tableName="Synonyms"
			showTable={state.entityStates[entityType].show}
			button={<Button label="Add Synonym" onClick={createNewSynonymHandler} className="w-4 p-button-text" />}
		/>
	);
};
