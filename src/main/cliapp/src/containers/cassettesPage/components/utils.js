/**
 * Readies the rows of a cassette component list for its pop-up: each gets its own `dataKey`.
 *
 * @param {Object[]} rows the rows as the API returned them
 * @returns {Object[]} copies; the argument is not modified
 */
export const prepareComponentRows = (rows) =>
	(rows ?? []).map((row) => ({ ...structuredClone(row), dataKey: crypto.randomUUID() }));

/**
 * A row as the replace endpoint takes it: without the pop-up's key, and without the cassette it belongs to, which is
 * the cassette the list is saved for.
 *
 * @param {Object} row
 * @returns {Object}
 */
export const stripComponentRow = (row) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, cassetteAssociationSubject, singleCassette, ...fields } = row;
	return fields;
};

/**
 * Marks each row whose linked entity was typed rather than picked from the autosuggest, so has no id, and which the
 * API could not read.
 *
 * @param {Object[]} rows
 * @param {Object} errorMessages the rows' current messages, by `dataKey`
 * @param {string} objectField the field holding the linked entity
 * @returns {Object|null} the messages with those rows marked, or null when there are none
 */
export const markRowsWithTypedObject = (rows, errorMessages, objectField) => {
	const typed = rows.filter((row) => row[objectField] && !row[objectField].id);
	if (typed.length === 0) return null;
	const marked = structuredClone(errorMessages);
	typed.forEach((row) => {
		marked[row.dataKey] = {
			...marked[row.dataKey],
			[objectField]: { severity: 'error', message: 'Select from the autosuggest' },
		};
	});
	return marked;
};

/**
 * Maps the API's index keyed row errors for a list onto the rows they belong to.
 *
 * @param {Object} data the rejected request's response body
 * @param {string} field the field the API files the rows' errors under
 * @param {Object[]} rows the rows as they were submitted
 * @returns {Object} the messages, by `dataKey` then field
 */
export const mapRowErrors = (data, field, rows) => {
	const indexed = data?.supplementalData?.errorMap?.[field] ?? {};
	const errorMessages = {};
	Object.keys(indexed).forEach((index) => {
		const row = rows[Number(index)];
		if (!row || typeof indexed[index] !== 'object') return;
		errorMessages[row.dataKey] = {};
		Object.keys(indexed[index]).forEach((rowField) => {
			errorMessages[row.dataKey][rowField] = { severity: 'error', message: indexed[index][rowField] };
		});
	});
	return errorMessages;
};
