import { useImmerReducer } from 'use-immer';
import { useCallback, useMemo } from 'react';
import { addDataKey, buildEmptyAlleleSymbol, generateCrossRefSearchFields, generateCurieSearchFields } from './utils';
import { getUniqueItemsByProperty } from '../../utils/utils';
import { Endpoints } from '../../constants/Endpoints';

const initialAlleleState = {
	allele: {
		taxon: {
			curie: '',
		},
		alleleSynonyms: [],
		alleleFullName: null,
		alleleSecondaryIds: [],
		alleleSymbol: null,
		alleleMutationTypes: [],
		alleleInheritanceModes: [],
		alleleFunctionalImpacts: [],
		alleleNomenclatureEvents: [],
		alleleGeneAssociations: [],
		alleleDatabaseStatus: null,
		alleleGermlineTransmissionStatus: null,
		references: [],
		inCollection: {
			name: '',
		},
		relatedNotes: [],
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	entityStates: {
		alleleSynonyms: {
			field: 'alleleSynonyms',
			endpoint: Endpoints.SlotAnnotation.ALLELE_SYNONYM,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		alleleFullName: {
			field: 'alleleFullName',
			endpoint: Endpoints.SlotAnnotation.ALLELE_FULL_NAME,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		alleleSecondaryIds: {
			field: 'alleleSecondaryIds',
			endpoint: Endpoints.SlotAnnotation.ALLELE_SECONDARY_ID,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		alleleSymbol: {
			field: 'alleleSymbol',
			endpoint: Endpoints.SlotAnnotation.ALLELE_SYMBOL,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'object',
		},
		alleleNomenclatureEvents: {
			field: 'alleleNomenclatureEvents',
			endpoint: Endpoints.SlotAnnotation.ALLELE_NOMENCLATURE_EVENT,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		alleleMutationTypes: {
			field: 'alleleMutationTypes',
			endpoint: Endpoints.SlotAnnotation.ALLELE_MUTATION_TYPE,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		alleleInheritanceModes: {
			field: 'alleleInheritanceModes',
			endpoint: Endpoints.SlotAnnotation.ALLELE_INHERITANCE_MODE,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
		},
		alleleFunctionalImpacts: {
			field: 'alleleFunctionalImpacts',
			endpoint: Endpoints.SlotAnnotation.ALLELE_FUNCTIONAL_IMPACT,
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
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
		alleleGeneAssociations: {
			field: 'alleleGeneAssociations',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: 'table',
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

const processTable = (field, allele, draft) => {
	if (!allele) return;

	if (!allele[field]) {
		allele[field] = [];
		return;
	}

	let clonableEntities = structuredClone(allele[field]);
	clonableEntities.forEach((entity, index) => {
		addDataKey(entity);
		draft.entityStates[field].editingRows[`${entity.dataKey}`] = true;
	});

	allele[field] = clonableEntities;
	draft.entityStates[field].show = true;
};

const processDisplayTable = (field, allele, draft) => {
	if (!allele) return;

	if (!allele[field]) {
		allele[field] = [];
		return;
	}

	draft.entityStates[field].show = true;
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
			// Clone so generateCrossRefSearchFields (in-place mutation) doesn't run
			// on a frozen object (immer freezes draft.allele after produce).
			const allele = structuredClone(action.value);
			generateCrossRefSearchFields(allele.references);
			generateCurieSearchFields(allele.alleleGeneAssociations, 'evidence');

			let states = Object.values(draft.entityStates);

			states.forEach((state) => {
				if (state.type === 'table') processTable(state.field, allele, draft);
				if (state.type === 'object') processObject(state.field, allele, draft);
				if (state.type === 'display') processDisplayTable(state.field, allele, draft);
			});

			draft.allele = allele;
			break;
		case 'RESET':
			// Cloned rather than assigned: immer freezes the state it produces, so handing it the
			// initial state object would freeze the state every reducer starts from.
			draft.allele = structuredClone(initialState.allele);
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
			draft.allele[action.field] = action.value;
			break;
		case 'EDIT_ROW':
			draft.allele[action.entityType][action.index][action.field] = action.value;
			break;
		case 'EDIT_FILTERABLE_ROW':
			const row = draft.allele[action.entityType].find((row) => row.dataKey === action.dataKey);
			if (row) {
				row[action.field] = action.value;
			}
			break;
		case 'REPLACE_ROW':
			const index = draft.allele[action.entityType].findIndex((row) => row.dataKey === action.dataKey);
			if (index !== -1) {
				draft.allele[action.entityType][index] = action.newRow;
			}
			break;
		case 'EDIT_OBJECT':
			draft.allele[action.entityType][action.field] = action.value;
			break;
		case 'ADD_ROW':
			draft.allele[action.entityType].unshift(action.row);
			if (action.entityType === 'references') {
				draft.allele[action.entityType] = getUniqueItemsByProperty(draft.allele[action.entityType], 'curie');
			}
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'ADD_OBJECT':
			draft.allele[action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_ROW':
			draft.allele[action.entityType] = draft.allele[action.entityType].filter((row) => row.dataKey !== action.dataKey);
			if (draft.allele[action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
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
