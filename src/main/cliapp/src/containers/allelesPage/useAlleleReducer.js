import { useImmerReducer } from 'use-immer';
import { useCallback, useMemo } from 'react';
import { addDataKey, buildEmptyAlleleSymbol } from './utils';
import { Endpoints } from '../../constants/Endpoints';

const initialAlleleState = {
	allele: {
		taxon: {
			curie: '',
		},
		alleleFullName: null,
		alleleSymbol: null,
		alleleDatabaseStatus: null,
		alleleGermlineTransmissionStatus: null,
		inCollection: {
			name: '',
		},
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	entityStates: {
		alleleFullName: {
			field: 'alleleFullName',
			endpoint: Endpoints.SlotAnnotation.ALLELE_FULL_NAME,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		alleleSymbol: {
			field: 'alleleSymbol',
			endpoint: Endpoints.SlotAnnotation.ALLELE_SYMBOL,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		alleleGermlineTransmissionStatus: {
			field: 'alleleGermlineTransmissionStatus',
			endpoint: Endpoints.SlotAnnotation.ALLELE_GERMLINE_TRANSMISSION_STATUS,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		alleleDatabaseStatus: {
			field: 'alleleDatabaseStatus',
			endpoint: Endpoints.SlotAnnotation.ALLELE_DATABASE_STATUS,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
	},
	errorMessages: {},
	submitted: false,
};

/**
 * The state a form starts from, and the state `RESET` returns to.
 *
 * Every allele carries a symbol, and the symbol table has no way to remove one, so create mode
 * starts with an empty symbol open for editing instead of offering a button to add it.
 *
 * @param {string} mode 'create' or 'detail'
 * @returns {Object} a fresh allele state
 */
const buildInitialAlleleState = (mode) => {
	const initialState = structuredClone(initialAlleleState);

	if (mode === 'create') {
		const symbol = buildEmptyAlleleSymbol();
		initialState.allele.alleleSymbol = symbol;
		initialState.entityStates.alleleSymbol.show = true;
		initialState.entityStates.alleleSymbol.editingRows = { [symbol.dataKey]: true };
	}

	return initialState;
};

const processObject = (field, allele, draft) => {
	if (!allele) return;

	if (!allele[field]) return;

	addDataKey(allele[field]);
	draft.entityStates[field].editingRows[allele[field].dataKey] = true;
	draft.entityStates[field].show = true;
};

const alleleReducer = (draft, action, initialState) => {
	switch (action.type) {
		case 'SET':
			// Cloned so the row keys added below are not written to a frozen object (immer freezes
			// draft.allele after produce).
			const allele = structuredClone(action.value);

			let states = Object.values(draft.entityStates);

			states.forEach((state) => processObject(state.field, allele, draft));

			draft.allele = allele;
			break;
		case 'RESET':
			// Cloned so the state every reset starts from is never the object later mutated in place.
			draft.allele = structuredClone(initialState.allele);
			Object.values(draft.entityStates).forEach((state) => {
				const initialEntityState = initialState.entityStates[state.field];
				state.show = initialEntityState.show;
				state.editingRows = structuredClone(initialEntityState.editingRows);
				state.errorMessages = {};
			});
			// A row's editors hold their input state for as long as the row stays mounted, and a
			// seeded symbol row stays mounted across a reset. Its dataKey is the row's identity, so
			// a fresh one remounts the editors on the blank symbol.
			if (draft.allele.alleleSymbol) {
				addDataKey(draft.allele.alleleSymbol);
				draft.entityStates.alleleSymbol.editingRows = { [draft.allele.alleleSymbol.dataKey]: true };
			}
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		case 'EDIT':
			draft.allele[action.field] = action.value;
			break;
		case 'EDIT_OBJECT':
			draft.allele[action.entityType][action.field] = action.value;
			break;
		case 'ADD_OBJECT':
			draft.allele[action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_OBJECT':
			draft.allele[action.entityType] = null;
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

			let submitStates = Object.values(draft.entityStates);

			submitStates.forEach((state) => {
				state.errorMessages = {};
			});
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

/**
 * Allele form state and its dispatch.
 *
 * @param {string} [mode] 'create' to start from a form seeded for a new allele, otherwise 'detail'
 * @returns {{alleleState: Object, alleleDispatch: Function}}
 */
export const useAlleleReducer = (mode = 'detail') => {
	const initialState = useMemo(() => buildInitialAlleleState(mode), [mode]);
	const reducer = useCallback((draft, action) => alleleReducer(draft, action, initialState), [initialState]);
	const [alleleState, alleleDispatch] = useImmerReducer(reducer, initialState);
	return { alleleState, alleleDispatch };
};
