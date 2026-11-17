import { useCallback, useMemo } from 'react';
import { current, isDraft } from 'immer';
import { useImmerReducer } from 'use-immer';
import { addDataKey, buildEmptyNameSlotAnnotation } from '../../utils/rows';

// The gene's single-object sections, each edited as one row of a table.
const OBJECT_FIELDS = ['geneSymbol', 'geneFullName', 'geneSystematicName', 'gcrpCrossReference'];

const initialGeneState = {
	gene: {},
	entityStates: Object.fromEntries(
		OBJECT_FIELDS.map((field) => [field, { field, show: false, errorMessages: {}, editingRows: {} }])
	),
	errorMessages: {},
	submitted: false,
};

/**
 * The state a form starts from, and the state `RESET` returns to.
 *
 * Every gene carries a symbol, so create mode starts with an empty symbol open for editing instead of offering a
 * button to add it, and with blank taxon and gene type terms for their autosuggests to edit.
 *
 * @param {'detail'|'create'} mode
 * @returns {Object} a fresh gene state
 */
const buildInitialGeneState = (mode) => {
	const initialState = structuredClone(initialGeneState);

	if (mode === 'create') {
		const symbol = buildEmptyNameSlotAnnotation();
		initialState.gene = {
			taxon: { curie: '' },
			geneType: { curie: '' },
			geneSymbol: symbol,
			internal: false,
			obsolete: false,
		};
		initialState.entityStates.geneSymbol.show = true;
		initialState.entityStates.geneSymbol.editingRows = { [symbol.dataKey]: true };
	}

	return initialState;
};

const processObject = (field, gene, draft) => {
	draft.entityStates[field].editingRows = {};
	draft.entityStates[field].show = false;
	draft.entityStates[field].errorMessages = {};
	if (!gene[field]) return;

	addDataKey(gene[field]);
	draft.entityStates[field].editingRows[gene[field].dataKey] = true;
	draft.entityStates[field].show = true;
};

const geneReducer = (draft, action, initialState) => {
	switch (action.type) {
		case 'SET': {
			// Cloned so the row keys added below are not written to a frozen object (immer freezes draft.gene after
			// produce).
			const gene = structuredClone(action.value ?? {});
			OBJECT_FIELDS.forEach((field) => processObject(field, gene, draft));
			draft.gene = gene;
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		}
		case 'RESET': {
			const resetState = structuredClone(initialState);
			// A row's editors hold their input state for as long as the row stays mounted, and the create form's symbol
			// row stays mounted across a reset. Its dataKey is the row's identity, so a fresh one remounts the editors on
			// the blank symbol.
			if (resetState.gene.geneSymbol) {
				addDataKey(resetState.gene.geneSymbol);
				resetState.entityStates.geneSymbol.editingRows = { [resetState.gene.geneSymbol.dataKey]: true };
			}
			return resetState;
		}
		case 'EDIT':
			draft.gene[action.field] = action.value;
			break;
		case 'EDIT_OBJECT':
			draft.gene[action.entityType][action.field] = action.value;
			break;
		case 'UPDATE_OBJECT': {
			// Applied to the object as it is now, so updates dispatched in the same tick build on one another. A section
			// removed before the update lands is left removed.
			const value = draft.gene[action.entityType];
			if (value) draft.gene[action.entityType] = action.update(isDraft(value) ? current(value) : value);
			break;
		}
		case 'ADD_OBJECT':
			draft.gene[action.entityType] = action.value;
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_OBJECT':
			draft.gene[action.entityType] = null;
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
 * Gene form state and its dispatch.
 *
 * Actions: `SET` loads a gene, clearing every error; `RESET` returns to the state the form started from; `EDIT` sets
 * a field; `EDIT_OBJECT` sets a field of a single-object section; `UPDATE_OBJECT` replaces a single-object section with
 * `update` applied to it; `ADD_OBJECT` / `DELETE_OBJECT` add or remove one; `UPDATE_ERROR_MESSAGES` /
 * `UPDATE_TABLE_ERROR_MESSAGES` set the page's or a section's errors; `SUBMIT` clears them for a save.
 *
 * @param {'detail'|'create'} [mode] - 'create' starts from a blank gene with an empty symbol open for editing
 * @returns {{geneState: Object, geneDispatch: Function}}
 */
export const useGeneReducer = (mode = 'detail') => {
	const initialState = useMemo(() => buildInitialGeneState(mode), [mode]);
	const reducer = useCallback((draft, action) => geneReducer(draft, action, initialState), [initialState]);
	const [geneState, geneDispatch] = useImmerReducer(reducer, initialState);
	return { geneState, geneDispatch };
};
