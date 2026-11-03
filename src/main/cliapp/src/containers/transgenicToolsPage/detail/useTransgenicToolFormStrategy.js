import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';

/**
 * Table editing strategy for the transgenic tool detail page's tables: rows come from useTransgenicToolReducer
 * state and edits dispatch back to it by dataKey.
 *
 * @param {object} state - useTransgenicToolReducer state (`{transgenicTool, entityStates}`)
 * @param {Function} dispatch - useTransgenicToolReducer dispatch
 * @param {string} entityType - key into `state.transgenicTool` and `state.entityStates`
 * @returns {object} a strategy for TableEditorProvider
 */
export function useTransgenicToolFormStrategy({ state, dispatch, entityType }) {
	return useDataKeyRowsStrategy({
		rows: state.transgenicTool?.[entityType],
		errors: state.entityStates?.[entityType]?.errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW_BY_KEY', entityType, dataKey: rowKey, field, value }),
	});
}
