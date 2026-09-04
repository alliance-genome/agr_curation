/**
 * Table editing strategy for a PrimeReact row-edit table, where the edit buffer
 * is the source of truth and `onRowEditSave` reads it back. Rows are addressed by
 * their index on the current page.
 *
 * @param {object} errorMessages - map holding `{[rowIndex]: {[field]: error}}`
 * @param {object} [uiErrorMessages] - map holding client-side validation errors
 *   in the same shape
 * @returns {object} a strategy for TableEditorProvider
 */
export function usePrimeRowEditStrategy({ errorMessages, uiErrorMessages }) {
	return {
		errorLayout: 'overlay',
		keyOf: (editorOptions) => editorOptions.rowIndex,
		errorsAt: (rowKey, field) => [errorMessages?.[rowKey]?.[field], uiErrorMessages?.[rowKey]?.[field]],
		readRow: (editorOptions) => editorOptions.rowData,
		write: ({ editorOptions, value }) => editorOptions.editorCallback(value),
	};
}
