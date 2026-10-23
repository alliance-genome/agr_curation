import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SymbolFormTable } from '../../allelesPage/symbol/SymbolFormTable';
import { FullNameFormTable } from '../../allelesPage/fullName/FullNameFormTable';
import { buildEmptyAlleleSymbol } from '../../allelesPage/utils';

/**
 * The transgenic tool detail page's symbol or full name section: one name slot annotation held in the tool
 * reducer and saved with the tool. The symbol's table offers no deletion and the full name's does; each
 * offers the name types of its own term set.
 *
 * @param {object} state - useTransgenicToolReducer state
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @param {'transgenicToolSymbol'|'transgenicToolFullName'} entityType - the name the section edits
 * @param {string} tableName - the section's label
 * @param {string} addLabel - the label of the button adding a name when the tool has none
 * @returns {JSX.Element}
 */
export const NameSlotAnnotationForm = ({ state, dispatch, entityType, tableName, addLabel }) => {
	const tableRef = useRef(null);
	const isSymbol = entityType === 'transgenicToolSymbol';
	const NameFormTable = isSymbol ? SymbolFormTable : FullNameFormTable;
	const name = state.transgenicTool?.[entityType];
	const entityState = state.entityStates[entityType];

	const editName = (field, value) => dispatch({ type: 'EDIT_OBJECT', entityType, field, value });

	const createNewNameHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_OBJECT', entityType, value: buildEmptyAlleleSymbol() });
	};

	const deletionHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'DELETE_OBJECT', entityType });
		dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType, errorMessages: {} });
	};

	return (
		<FormTableWrapper
			table={
				<NameFormTable
					name={[name]}
					editingRows={entityState.editingRows}
					onRowEditChange={() => null}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={entityState.errorMessages}
					textOnChangeHandler={(rowIndex, event, field) => editName(field, event.target.value)}
					synonymScopeOnChangeHandler={(props, event) => {
						props.editorCallback?.(event.target.value);
						editName('synonymScope', event.target.value);
					}}
					nameTypeOnChangeHandler={(props, event) => {
						props.editorCallback?.(event.target.value);
						editName('nameType', event.target.value);
					}}
					internalOnChangeHandler={(props, event) => {
						props.editorCallback?.(event.target.value?.name);
						editName('internal', event.target?.value?.name);
					}}
					evidenceOnChangeHandler={(event, setFieldValue) => {
						setFieldValue(event.target.value);
						editName('evidence', event.target.value);
					}}
				/>
			}
			tableName={tableName}
			showTable={entityState.show}
			button={
				<Button label={addLabel} onClick={createNewNameHandler} disabled={!!name} className="w-4 p-button-text" />
			}
		/>
	);
};
