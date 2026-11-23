import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';

/**
 * Table editing strategy for a detail page's tables (the transgenic tool's, the cassette's): rows come from the
 * page's useDetailFormReducer state and edits dispatch back to it by dataKey.
 *
 * @param {object} state - the page's reducer state (`{[entityKey], entityStates}`)
 * @param {Function} dispatch - the page's reducer dispatch
 * @param {string} entityType - key into `state[entityKey]` and `state.entityStates`
 * @param {string} [entityKey] - the key the page's entity is held under
 * @returns {object} a strategy for TableEditorProvider
 */
export function useTransgenicToolFormStrategy({ state, dispatch, entityType, entityKey = 'transgenicTool' }) {
	return useDataKeyRowsStrategy({
		rows: state[entityKey]?.[entityType],
		errors: state.entityStates?.[entityType]?.errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW_BY_KEY', entityType, dataKey: rowKey, field, value }),
	});
}
