import { useImmerReducer } from 'use-immer';
import { useCallback, useMemo } from 'react';
import { addDataKey, generateCrossRefSearchFields } from './utils';
import { getUniqueItemsByProperty } from '../../utils/utils';
import { Endpoints } from '../../constants/Endpoints';

const initialVariantState = {
	variant: {
		taxon: {
			curie: '',
		},
		variantType: null,
		variantStatus: null,
		sourceGeneralConsequence: null,
		relatedNotes: [],
		references: [],
		synonyms: [],
		crossReferences: [],
		internal: false,
		obsolete: false,
	},
	entityStates: {
		relatedNotes: {
			field: 'relatedNotes',
			endpoint: Endpoints.Entity.NOTE,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		references: {
			field: 'references',
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

const processTable = (field, variant, draft) => {
	if (!variant) return;

	if (!variant[field]) {
		variant[field] = [];
		return;
	}

	let clonableEntities = structuredClone(variant[field]);
	clonableEntities.forEach((entity) => {
		addDataKey(entity);
		draft.entityStates[field].editingRows[`${entity.dataKey}`] = true;
	});

	variant[field] = clonableEntities;
	draft.entityStates[field].show = true;
};

const processDisplayTable = (field, variant, draft) => {
	if (!variant) return;

	if (!variant[field]) {
		variant[field] = [];
		return;
	}

	draft.entityStates[field].show = true;
};

const variantReducer = (draft, action, initialState) => {
	switch (action.type) {
		case 'SET': {
			// Cloned so the in-place mutations below never run on a frozen object (immer freezes
			// draft.variant after produce).
			const variant = structuredClone(action.value);
			generateCrossRefSearchFields(variant.references);

			Object.values(draft.entityStates).forEach((state) => {
				if (state.type === 'table') processTable(state.field, variant, draft);
				if (state.type === 'display') processDisplayTable(state.field, variant, draft);
			});

			draft.variant = variant;
			break;
		}
		case 'RESET':
			draft.variant = structuredClone(initialState.variant);
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
			draft.variant[action.field] = action.value;
			break;
		case 'EDIT_ROW':
			draft.variant[action.entityType][action.index][action.field] = action.value;
			break;
		case 'ADD_ROW':
			draft.variant[action.entityType].unshift(action.row);
			if (action.entityType === 'references') {
				draft.variant[action.entityType] = getUniqueItemsByProperty(draft.variant[action.entityType], 'curie');
			}
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_ROW':
			draft.variant[action.entityType] = draft.variant[action.entityType].filter(
				(row) => row.dataKey !== action.dataKey
			);
			if (draft.variant[action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
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
 * Variant form state and its dispatch.
 *
 * @returns {{variantState: Object, variantDispatch: Function}}
 */
export const useVariantReducer = () => {
	const initialState = useMemo(() => structuredClone(initialVariantState), []);
	const reducer = useCallback((draft, action) => variantReducer(draft, action, initialState), [initialState]);
	const [variantState, variantDispatch] = useImmerReducer(reducer, initialState);
	return { variantState, variantDispatch };
};
