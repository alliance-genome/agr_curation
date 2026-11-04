import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SymbolFormTable } from '../../allelesPage/symbol/SymbolFormTable';
import { FullNameFormTable } from '../../allelesPage/fullName/FullNameFormTable';
import { buildEmptyAlleleSymbol } from '../../allelesPage/utils';

/**
 * A detail page's symbol or full name section (the transgenic tool's, the cassette's): one name slot annotation
 * held in the page's reducer and saved with the entity. The symbol's table offers no deletion and the full
 * name's does; each offers the name types of its own term set.
 *
 * @param {object} state - the page's reducer state
 * @param {Function} dispatch - the page's reducer dispatch
 * @param {string} [entityKey] - the key the page's entity is held under
 * @param {string} entityType - the name the section edits, such as `transgenicToolSymbol` or `cassetteFullName`;
 *   a field ending in `Symbol` is edited as a symbol
 * @param {string} tableName - the section's label
 * @param {string} addLabel - the label of the button adding a name when the entity has none
 * @returns {JSX.Element}
 */
export const NameSlotAnnotationForm = ({
	state,
	dispatch,
	entityKey = 'transgenicTool',
	entityType,
	tableName,
	addLabel,
}) => {
	const tableRef = useRef(null);
	const isSymbol = entityType.endsWith('Symbol');
	const NameFormTable = isSymbol ? SymbolFormTable : FullNameFormTable;
	const name = state[entityKey]?.[entityType];
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
					// The name is the reducer's, which freezes it, so edits go through dispatch alone:
					// editorCallback would write into the frozen row and throw before the dispatch (SCRUM-6655)
					synonymScopeOnChangeHandler={(props, event) => editName('synonymScope', event.target.value)}
					nameTypeOnChangeHandler={(props, event) => editName('nameType', event.target.value)}
					internalOnChangeHandler={(props, event) => editName('internal', event.target?.value?.name)}
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
