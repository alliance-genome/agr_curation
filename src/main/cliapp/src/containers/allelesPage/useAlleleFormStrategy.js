import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';

/**
 * Table editing strategy for the allele detail page form tables: rows come from
 * useAlleleReducer state and edits dispatch back to it, since these tables define
 * no onRowEditSave and PrimeReact's edit buffer is never read back.
 *
 * @param {object} state - useAlleleReducer state (`{allele, entityStates}`)
 * @param {Function} dispatch - useAlleleReducer dispatch
 * @param {string} entityType - key into `state.allele` and `state.entityStates`
 * @returns {object} a strategy for TableEditorProvider
 */
export function useAlleleFormStrategy({ state, dispatch, entityType }) {
	return useDataKeyRowsStrategy({
		rows: state.allele?.[entityType],
		errors: state.entityStates?.[entityType]?.errorMessages,
		write: ({ rowKey, field, value }) =>
			dispatch({ type: 'EDIT_ROW_BY_KEY', entityType, dataKey: rowKey, field, value }),
	});
}
