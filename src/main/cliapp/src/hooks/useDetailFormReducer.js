import { useCallback, useMemo } from 'react';
import { useImmerReducer } from 'use-immer';
import { addDataKey } from '../containers/allelesPage/utils';
import { prepareReferences } from '../containers/allelesPage/referencesTable/utils';
import { getUniqueItemsByProperty } from '../utils/utils';

const buildInitialState = (entityKey, entityTypes) => ({
	[entityKey]: {},
	entityStates: Object.fromEntries(
		Object.keys(entityTypes).map((field) => [
			field,
			{ field, type: entityTypes[field], show: false, errorMessages: {}, editingRows: {} },
		])
	),
	errorMessages: {},
	// Whether the form holds an edit not yet saved. Any edit sets it and loading the entity clears it.
	hasPendingEdits: false,
});

const processTable = (field, entity, draft) => {
	const rows = (entity[field] ?? []).map((row) => {
		const clone = structuredClone(row);
		if (!clone.dataKey) addDataKey(clone);
		return clone;
	});
	draft.entityStates[field].editingRows = Object.fromEntries(rows.map((row) => [row.dataKey, true]));
	draft.entityStates[field].show = rows.length > 0;
	entity[field] = rows;
};

const processObject = (field, entity, draft) => {
	draft.entityStates[field].editingRows = {};
	draft.entityStates[field].show = false;
	if (!entity[field]) return;

	addDataKey(entity[field]);
	draft.entityStates[field].editingRows[entity[field].dataKey] = true;
	draft.entityStates[field].show = true;
};

const detailFormReducer = (entityKey, entityTypes) => (draft, action) => {
	switch (action.type) {
		case 'SET': {
			// Cloned so the in-place changes below never run on a frozen object (immer freezes the entity after
			// produce).
			const entity = structuredClone(action.value ?? {});
			if ('references' in entityTypes) entity.references = prepareReferences(entity.references);

			Object.entries(entityTypes).forEach(([field, type]) => {
				if (type === 'table') processTable(field, entity, draft);
				if (type === 'object') processObject(field, entity, draft);
				draft.entityStates[field].errorMessages = {};
			});

			draft[entityKey] = entity;
			draft.errorMessages = {};
			draft.hasPendingEdits = false;
			break;
		}
		case 'EDIT':
			draft[entityKey][action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'EDIT_ROW':
			draft[entityKey][action.entityType][action.index][action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'EDIT_ROW_BY_KEY': {
			const row = draft[entityKey][action.entityType].find((candidate) => candidate.dataKey === action.dataKey);
			if (row) row[action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		}
		case 'REPLACE_ROW': {
			const rows = draft[entityKey][action.entityType];
			const index = rows.findIndex((candidate) => candidate.dataKey === action.row.dataKey);
			if (index !== -1) rows[index] = action.row;
			draft.hasPendingEdits = true;
			break;
		}
		case 'EDIT_OBJECT':
			draft[entityKey][action.entityType][action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'ADD_ROW':
			if (!draft[entityKey][action.entityType]) draft[entityKey][action.entityType] = [];
			if (action.append) {
				draft[entityKey][action.entityType].push(action.row);
			} else {
				draft[entityKey][action.entityType].unshift(action.row);
			}
			// A reference already on the entity is not added twice
			if (action.entityType === 'references') {
				draft[entityKey].references = getUniqueItemsByProperty(draft[entityKey].references, 'curie');
			}
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			draft.hasPendingEdits = true;
			break;
		case 'ADD_OBJECT':
			draft[entityKey][action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			draft.hasPendingEdits = true;
			break;
		case 'DELETE_ROW':
			draft[entityKey][action.entityType] = draft[entityKey][action.entityType].filter(
				(row) => row.dataKey !== action.dataKey
			);
			if (draft[entityKey][action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
			draft.hasPendingEdits = true;
			break;
		case 'DELETE_OBJECT':
			draft[entityKey][action.entityType] = null;
			draft.entityStates[action.entityType].show = false;
			draft.hasPendingEdits = true;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'UPDATE_TABLE_ERROR_MESSAGES':
			// The API may report errors on a field the page holds no table for; those stay in the flat messages
			if (draft.entityStates[action.entityType]) {
				draft.entityStates[action.entityType].errorMessages = action.errorMessages;
			}
			break;
		case 'SUBMIT':
			draft.errorMessages = {};
			Object.values(draft.entityStates).forEach((state) => {
				state.errorMessages = {};
			});
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

/**
 * The form state of a detail page whose entity is edited in place and saved as a whole, and its dispatch
 * (SCRUM-6655, SCRUM-6747). The entity is held under `state[entityKey]`; each of its sections edited in a table
 * has an entry in `state.entityStates`.
 *
 * @param {string} entityKey the key the entity is held under, such as `'transgenicTool'`
 * @param {Object<string, 'object'|'table'>} entityTypes the entity's sections edited in a table: 'object' holds one
 *   row, 'table' a list. Must keep its identity across renders.
 * @returns {[Object, Function]} the state and its dispatch
 */
export const useDetailFormReducer = (entityKey, entityTypes) => {
	const initialState = useMemo(() => buildInitialState(entityKey, entityTypes), [entityKey, entityTypes]);
	const reducer = useMemo(() => detailFormReducer(entityKey, entityTypes), [entityKey, entityTypes]);
	const stableReducer = useCallback((draft, action) => reducer(draft, action), [reducer]);
	return useImmerReducer(stableReducer, initialState);
};
