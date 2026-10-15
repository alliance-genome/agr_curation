import { useImmerReducer } from 'use-immer';
import { useCallback, useMemo } from 'react';
import { addDataKey } from './utils';
import { Endpoints } from '../../constants/Endpoints';

const initialAGMState = {
	agm: {
		taxon: {
			curie: '',
		},
		subtype: null,
		agmFullName: null,
		agmSynonyms: [],
		agmSecondaryIds: [],
		crossReferences: [],
		internal: false,
		obsolete: false,
	},
	entityStates: {
		agmFullName: {
			field: 'agmFullName',
			endpoint: Endpoints.SlotAnnotation.AGM_FULL_NAME,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		agmSynonyms: {
			field: 'agmSynonyms',
			endpoint: Endpoints.SlotAnnotation.AGM_SYNONYM,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		agmSecondaryIds: {
			field: 'agmSecondaryIds',
			endpoint: Endpoints.SlotAnnotation.AGM_SECONDARY_ID,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		crossReferences: {
			field: 'crossReferences',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'display',
		},
	},
	errorMessages: {},
	submitted: false,
};

const processTable = (field, agm, draft) => {
	if (!agm) return;

	if (!agm[field]) {
		agm[field] = [];
		return;
	}

	let clonableEntities = structuredClone(agm[field]);
	clonableEntities.forEach((entity) => {
		addDataKey(entity);
		draft.entityStates[field].editingRows[`${entity.dataKey}`] = true;
	});

	agm[field] = clonableEntities;
	draft.entityStates[field].show = true;
};

const processDisplayTable = (field, agm, draft) => {
	if (!agm) return;

	if (!agm[field]) {
		agm[field] = [];
		return;
	}

	draft.entityStates[field].show = true;
};

const processObject = (field, agm, draft) => {
	if (!agm) return;

	if (!agm[field]) return;

	addDataKey(agm[field]);
	draft.entityStates[field].editingRows[agm[field].dataKey] = true;
	draft.entityStates[field].show = true;
};

const agmReducer = (draft, action, initialState) => {
	switch (action.type) {
		case 'SET': {
			// Cloned so the in-place mutations below never run on a frozen object (immer freezes
			// draft.agm after produce).
			const agm = structuredClone(action.value);

			Object.values(draft.entityStates).forEach((state) => {
				if (state.type === 'table') processTable(state.field, agm, draft);
				if (state.type === 'object') processObject(state.field, agm, draft);
				if (state.type === 'display') processDisplayTable(state.field, agm, draft);
			});

			draft.agm = agm;
			break;
		}
		case 'RESET':
			draft.agm = structuredClone(initialState.agm);
			Object.values(draft.entityStates).forEach((state) => {
				const initialEntityState = initialState.entityStates[state.field];
				state.show = initialEntityState.show;
				state.editingRows = structuredClone(initialEntityState.editingRows);
				state.errorMessages = {};
			});
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		case 'EDIT':
			draft.agm[action.field] = action.value;
			break;
		case 'EDIT_ROW':
			draft.agm[action.entityType][action.index][action.field] = action.value;
			break;
		case 'EDIT_OBJECT':
			draft.agm[action.entityType][action.field] = action.value;
			break;
		case 'ADD_ROW':
			draft.agm[action.entityType].unshift(action.row);
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'ADD_OBJECT':
			draft.agm[action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_ROW':
			draft.agm[action.entityType] = draft.agm[action.entityType].filter((row) => row.dataKey !== action.dataKey);
			if (draft.agm[action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
			break;
		case 'DELETE_OBJECT':
			draft.agm[action.entityType] = null;
			draft.entityStates[action.entityType].show = false;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'UPDATE_TABLE_ERROR_MESSAGES':
			draft.entityStates[action.entityType].errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
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
 * AGM form state and its dispatch.
 *
 * @returns {{agmState: Object, agmDispatch: Function}}
 */
export const useAGMReducer = () => {
	const initialState = useMemo(() => structuredClone(initialAGMState), []);
	const reducer = useCallback((draft, action) => agmReducer(draft, action, initialState), [initialState]);
	const [agmState, agmDispatch] = useImmerReducer(reducer, initialState);
	return { agmState, agmDispatch };
};
