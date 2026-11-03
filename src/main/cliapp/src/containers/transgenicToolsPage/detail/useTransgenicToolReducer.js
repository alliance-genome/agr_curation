import { useCallback, useMemo } from 'react';
import { useImmerReducer } from 'use-immer';
import { addDataKey } from '../../allelesPage/utils';
import { prepareReferences } from '../../allelesPage/referencesTable/utils';
import { getUniqueItemsByProperty } from '../../../utils/utils';

// The tool's sections whose rows are edited in a table: 'object' holds one row, 'table' a list.
const ENTITY_TYPES = {
	transgenicToolSymbol: 'object',
	transgenicToolFullName: 'object',
	transgenicToolSynonyms: 'table',
	transgenicToolUses: 'table',
	references: 'table',
	relatedNotes: 'table',
	crossReferences: 'table',
};

const initialTransgenicToolState = {
	transgenicTool: {},
	entityStates: Object.fromEntries(
		Object.keys(ENTITY_TYPES).map((field) => [
			field,
			{ field, type: ENTITY_TYPES[field], show: false, errorMessages: {}, editingRows: {} },
		])
	),
	errorMessages: {},
	// Whether the form holds an edit not yet saved. Any edit sets it and loading the tool clears it.
	hasPendingEdits: false,
};

const processTable = (field, transgenicTool, draft) => {
	const rows = (transgenicTool[field] ?? []).map((row) => {
		const clone = structuredClone(row);
		if (!clone.dataKey) addDataKey(clone);
		return clone;
	});
	draft.entityStates[field].editingRows = Object.fromEntries(rows.map((row) => [row.dataKey, true]));
	draft.entityStates[field].show = rows.length > 0;
	transgenicTool[field] = rows;
};

const processObject = (field, transgenicTool, draft) => {
	draft.entityStates[field].editingRows = {};
	draft.entityStates[field].show = false;
	if (!transgenicTool[field]) return;

	addDataKey(transgenicTool[field]);
	draft.entityStates[field].editingRows[transgenicTool[field].dataKey] = true;
	draft.entityStates[field].show = true;
};

const transgenicToolReducer = (draft, action) => {
	switch (action.type) {
		case 'SET': {
			// Cloned so the in-place changes below never run on a frozen object (immer freezes
			// draft.transgenicTool after produce).
			const transgenicTool = structuredClone(action.value ?? {});
			transgenicTool.references = prepareReferences(transgenicTool.references);

			Object.entries(ENTITY_TYPES).forEach(([field, type]) => {
				if (type === 'table') processTable(field, transgenicTool, draft);
				if (type === 'object') processObject(field, transgenicTool, draft);
				draft.entityStates[field].errorMessages = {};
			});

			draft.transgenicTool = transgenicTool;
			draft.errorMessages = {};
			draft.hasPendingEdits = false;
			break;
		}
		case 'EDIT':
			draft.transgenicTool[action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'EDIT_ROW':
			draft.transgenicTool[action.entityType][action.index][action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'EDIT_ROW_BY_KEY': {
			const row = draft.transgenicTool[action.entityType].find((candidate) => candidate.dataKey === action.dataKey);
			if (row) row[action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		}
		case 'REPLACE_ROW': {
			const rows = draft.transgenicTool[action.entityType];
			const index = rows.findIndex((candidate) => candidate.dataKey === action.row.dataKey);
			if (index !== -1) rows[index] = action.row;
			draft.hasPendingEdits = true;
			break;
		}
		case 'EDIT_OBJECT':
			draft.transgenicTool[action.entityType][action.field] = action.value;
			draft.hasPendingEdits = true;
			break;
		case 'ADD_ROW':
			if (action.append) {
				draft.transgenicTool[action.entityType].push(action.row);
			} else {
				draft.transgenicTool[action.entityType].unshift(action.row);
			}
			// A reference already on the tool is not added twice
			if (action.entityType === 'references') {
				draft.transgenicTool.references = getUniqueItemsByProperty(draft.transgenicTool.references, 'curie');
			}
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			draft.hasPendingEdits = true;
			break;
		case 'ADD_OBJECT':
			draft.transgenicTool[action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			draft.hasPendingEdits = true;
			break;
		case 'DELETE_ROW':
			draft.transgenicTool[action.entityType] = draft.transgenicTool[action.entityType].filter(
				(row) => row.dataKey !== action.dataKey
			);
			if (draft.transgenicTool[action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
			draft.hasPendingEdits = true;
			break;
		case 'DELETE_OBJECT':
			draft.transgenicTool[action.entityType] = null;
			draft.entityStates[action.entityType].show = false;
			draft.hasPendingEdits = true;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'UPDATE_TABLE_ERROR_MESSAGES':
			draft.entityStates[action.entityType].errorMessages = action.errorMessages;
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
 * Transgenic tool detail page form state and its dispatch (SCRUM-6655). The tool's own fields and lists are
 * held here and saved with the tool; its tool associations are a sub-resource of their own
 * (useTransgenicToolAssociations).
 *
 * @returns {{transgenicToolState: Object, transgenicToolDispatch: Function}}
 */
export const useTransgenicToolReducer = () => {
	const initialState = useMemo(() => structuredClone(initialTransgenicToolState), []);
	const reducer = useCallback((draft, action) => transgenicToolReducer(draft, action), []);
	const [transgenicToolState, transgenicToolDispatch] = useImmerReducer(reducer, initialState);
	return { transgenicToolState, transgenicToolDispatch };
};
