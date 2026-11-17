import { useDataKeyRowsStrategy } from './useDataKeyRowsStrategy';

/**
 * Table editing strategy for a table whose rows are held in local component state —
 * cloned on entry, edited in isolation, handed back to their owner on save.
 *
 * Addresses rows by `dataKey` rather than by position, matching how these tables key
 * their `editingRows` and their validation errors. Consistency is the point: a strategy
 * that reads one key and writes the other stores a curator's edit against the wrong
 * row, which no error surfaces.
 *
 * @param {object[]} rows - the current rows, each carrying a `dataKey`
 * @param {Function} setRows - the rows' useState setter
 * @param {object} [errors] - errors keyed `{[dataKey]: {[field]: error}}`
 * @param {'overlay'|'inline'|'text'} [errorLayout] - defaults to `inline`
 * @returns {object} a strategy for TableEditorProvider
 */
export function useLocalRowsStrategy({ rows, setRows, errors, errorLayout }) {
	return useDataKeyRowsStrategy({
		rows,
		errors,
		errorLayout,
		write: ({ rowKey, field, value }) =>
			setRows((previous) => (previous ?? []).map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row))),
	});
}
