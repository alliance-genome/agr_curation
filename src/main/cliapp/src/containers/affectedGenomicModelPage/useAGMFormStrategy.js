import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';

/**
 * Table editing strategy for the AGM form tables on the detail and create pages: rows come
 * from useAGMReducer state and edits dispatch back to it by dataKey.
 *
 * @param {object} state - useAGMReducer state (`{agm, entityStates}`)
 * @param {Function} dispatch - useAGMReducer dispatch
 * @param {string} entityType - key into `state.agm` and `state.entityStates`
 * @returns {object} a strategy for TableEditorProvider
 */
export function useAGMFormStrategy({ state, dispatch, entityType }) {
	return useDataKeyRowsStrategy({
		rows: state.agm?.[entityType],
		errors: state.entityStates?.[entityType]?.errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW_BY_KEY', entityType, dataKey: rowKey, field, value }),
	});
}
