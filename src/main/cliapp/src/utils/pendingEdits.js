/**
 * Adds a Pending Edits warning for one field to a row's errors, keeping the row's other errors and every other
 * row's.
 *
 * @param {object} [errorMessages] - a table's errors, keyed by row then field
 * @param {string|number} rowKey - the row's key in `errorMessages`
 * @param {string} field - the field the warning is shown under
 * @returns {object} the errors with the warning added
 */
export const markPendingEdits = (errorMessages, rowKey, field) => ({
	...errorMessages,
	[rowKey]: {
		...errorMessages?.[rowKey],
		[field]: { severity: 'warn', message: 'Pending Edits!' },
	},
});
