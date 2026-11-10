/**
 * Table editing strategy for a table whose rows live outside PrimeReact's edit
 * buffer — in a reducer, in component state, anywhere the caller owns. Rows are
 * addressed by their `dataKey`, the same key these tables use for `editingRows` and
 * for their validation errors, so one key identifies a row everywhere.
 *
 * Reads the row from `rows` rather than from `editorOptions.rowData`, which is the
 * part that is load-bearing: in a table that keeps every row in edit mode PrimeReact
 * never refreshes the row data it captured when the editor mounted, so a controlled
 * widget reading `rowData` renders a stale value. `rowIndex` is current on every render
 * but shifts when rows are added or deleted, so rows are addressed by `dataKey` instead.
 *
 * Knows nothing about where the rows came from or how they are stored. The caller
 * supplies `write`; see useAGMFormStrategy for a reducer-backed example.
 *
 * @param {object[]} rows - the current rows, each carrying a `dataKey`
 * @param {object} [errors] - errors keyed `{[dataKey]: {[field]: error}}`
 * @param {(change: {editorOptions: object, rowKey: any, field: string, value: any}) => void} write -
 *   stores an edited value
 * @param {'overlay'|'inline'|'text'} [errorLayout] - defaults to `inline`
 * @returns {object} a strategy for TableEditorProvider
 */
export function useDataKeyRowsStrategy({ errorLayout = 'inline', rows, errors, write }) {
	return {
		errorLayout,
		keyOf: (editorOptions) => editorOptions.rowData?.dataKey,
		errorsAt: (rowKey, field) => [errors?.[rowKey]?.[field]],
		readRow: (editorOptions) => {
			const rowKey = editorOptions.rowData?.dataKey;
			if (rowKey == null) return editorOptions.rowData;
			return rows?.find((row) => row.dataKey === rowKey) ?? editorOptions.rowData;
		},
		write,
	};
}
