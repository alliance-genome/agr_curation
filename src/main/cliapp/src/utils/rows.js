/**
 * Gives a row a key of its own, for a table to track it by.
 *
 * @param {Object} entity the row, which is changed in place
 */
export const addDataKey = (entity) => {
	entity.dataKey = crypto.randomUUID();
};

/**
 * @param {Object[]} [entities]
 * @returns {string[]} the entities' curies in sorted order, to compare lists by whatever order they are in; none
 *   when there is no list
 */
export const sortedCuries = (entities) =>
	Array.isArray(entities) ? entities.map((entity) => entity?.curie).sort() : [];

/**
 * @param {(row: Object) => string} valueOf a row's value to compare by
 * @returns {(rows: Object[], otherRows: Object[]) => boolean} whether two lists hold rows of the same value,
 *   row by row
 */
export const sameRowsBy = (valueOf) => (rows, otherRows) =>
	rows.length === otherRows.length && rows.every((row, index) => valueOf(row) === valueOf(otherRows[index]));

/**
 * An empty name slot annotation, ready to be edited in a symbol or name table.
 *
 * @returns {Object} a name with blank text fields and no name type
 */
export const buildEmptyNameSlotAnnotation = () => ({
	dataKey: 0,
	synonymUrl: '',
	internal: false,
	nameType: null,
	formatText: '',
	displayText: '',
});

/**
 * DataTable `onRowEditChange` handler for the form tables, which keep every row in
 * edit mode. PrimeReact ignores the `editingRows` prop unless a handler is supplied,
 * and every row then falls back to its own internal state and renders read-only body
 * cells. There is nothing to update, so the handler does nothing.
 */
export const KEEP_ROWS_IN_EDIT_MODE = () => null;
