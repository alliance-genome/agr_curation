import { useMemo } from 'react';

/**
 * The row edits a sub-resource section's table makes, all of them on the rows `useSubResourceList` holds.
 * Every row is always in edit mode.
 *
 * @param {Object} subResource what `useSubResourceList` returns
 * @param {Object} [options]
 * @param {boolean} [options.prepend] whether a new row goes above the others rather than below them
 */
export const useSubResourceRows = ({ rows, setRows, setErrorMessages }, { prepend = false } = {}) => {
	const editingRows = useMemo(() => Object.fromEntries(rows.map((row) => [row.dataKey, true])), [rows]);

	/** @param {Object} row a row with its own `dataKey` */
	const addRow = (row) => setRows((previous) => (prepend ? [row, ...previous] : [...previous, row]));

	/**
	 * @param {string} dataKey
	 * @param {(row: Object) => Object} change gives the row as it is to be
	 */
	const updateRow = (dataKey, change) =>
		setRows((previous) => previous.map((row) => (row.dataKey === dataKey ? change(row) : row)));

	/**
	 * Removes a row and the marks on it, as a table's delete button does.
	 *
	 * @param {Event} event
	 * @param {string} dataKey
	 */
	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setRows((previous) => previous.filter((row) => row.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const remaining = { ...previous };
			delete remaining[dataKey];
			return remaining;
		});
	};

	// Every row is always in edit mode, so the table has no edit mode to change.
	const onRowEditChange = () => null;

	return { editingRows, addRow, updateRow, deletionHandler, onRowEditChange };
};
