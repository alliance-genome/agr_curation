import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../FormTableWrapper';
import { SymbolFormTable } from './SymbolFormTable';
import { FullNameFormTable } from './FullNameFormTable';
import { buildEmptyNameSlotAnnotation } from '../../utils/rows';

/**
 * A detail page's symbol or name section: one name slot annotation held in the page's reducer and saved with the
 * entity. The symbol's table offers no deletion and the name's does; the symbol's offers the symbol name types, and
 * the name's those of `nameTypeVocabType`.
 *
 * @param {object} props
 * @param {object} [props.name] - the name the entity holds, if any
 * @param {object} props.entityState - the reducer's `entityStates` entry for the name
 * @param {Function} props.dispatch - the reducer's dispatch
 * @param {string} props.entityType - the name's field on the entity, and its `entityStates` key
 * @param {boolean} [props.isSymbol] - whether the name is a symbol rather than a full or systematic name
 * @param {string} [props.nameTypeVocabType] - the term set a full or systematic name's types come from; the full
 *   name's by default
 * @param {string} props.tableName - the section's label
 * @param {string} props.addLabel - the label of the button adding a name when the entity has none
 * @param {boolean} [props.isPending] - whether to mark the section as having pending edits
 * @param {boolean} [props.required] - whether to mark the section as required
 * @param {boolean} [props.showAddButton] - whether to offer the button adding a name
 * @returns {JSX.Element}
 */
export const NameSlotAnnotationForm = ({
	name,
	entityState,
	dispatch,
	entityType,
	isSymbol = false,
	nameTypeVocabType,
	tableName,
	addLabel,
	isPending = false,
	required = false,
	showAddButton = true,
}) => {
	const tableRef = useRef(null);
	const NameFormTable = isSymbol ? SymbolFormTable : FullNameFormTable;

	const editName = (field, value) => dispatch({ type: 'EDIT_OBJECT', entityType, field, value });

	const createNewNameHandler = (event) => {
		event.preventDefault();
		dispatch({ type: 'ADD_OBJECT', entityType, value: buildEmptyNameSlotAnnotation() });
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
					nameTypeVocabType={nameTypeVocabType}
				/>
			}
			tableName={tableName}
			showTable={entityState.show}
			isPending={isPending}
			required={required}
			button={
				showAddButton ? (
					<Button label={addLabel} onClick={createNewNameHandler} disabled={!!name} className="w-4 p-button-text" />
				) : null
			}
		/>
	);
};
