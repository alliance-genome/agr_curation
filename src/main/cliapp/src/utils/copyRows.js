// The fields a stored row carries that belong to it alone, and so cannot be carried onto a copy.
const ROW_IDENTITY_FIELDS = [
	'id',
	'createdBy',
	'updatedBy',
	'dateCreated',
	'dateUpdated',
	'dbDateCreated',
	'dbDateUpdated',
];

/**
 * A stored row as a new one to create: a deep copy without its id or audit fields.
 *
 * @param {Object} [row]
 * @returns {Object|null} the copy, or null when there is no row
 */
export const copyRow = (row) => {
	if (!row) return null;
	const copy = structuredClone(row);
	ROW_IDENTITY_FIELDS.forEach((field) => delete copy[field]);
	return copy;
};

/**
 * Stored rows as new ones to create, each copied as {@link copyRow} copies it.
 *
 * @param {Object[]} [rows]
 * @returns {Object[]|undefined} the copies, or undefined when there are no rows
 */
export const copyRows = (rows) => (rows?.length ? rows.map(copyRow) : undefined);
