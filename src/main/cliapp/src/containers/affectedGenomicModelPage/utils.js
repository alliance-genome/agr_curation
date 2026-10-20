import { getIdentifier } from '../../utils/utils';

export const addDataKey = (entity) => {
	entity.dataKey = crypto.randomUUID();
};

// STR has a plain name string, not a symbol/full-name slot annotation, so its label is built
// directly rather than read off a displayText.
export const strLabel = (str) => {
	if (!str) return '';
	return str.name ? `${str.name} (${getIdentifier(str)})` : getIdentifier(str);
};

const ROW_IDENTITY_FIELDS = [
	'id',
	'createdBy',
	'updatedBy',
	'dateCreated',
	'dateUpdated',
	'dbDateCreated',
	'dbDateUpdated',
];

const copyRow = (row) => {
	if (!row) return null;
	const copy = structuredClone(row);
	ROW_IDENTITY_FIELDS.forEach((field) => delete copy[field]);
	return copy;
};

const copyRows = (rows) => (rows?.length ? rows.map(copyRow) : undefined);

/**
 * A new AGM carrying the fields of a stored one that the create form edits, for the create page
 * to start from.
 *
 * Left behind: everything that identifies the stored AGM (its ids and secondary IDs), its data
 * provider, cross references, STR associations and audit fields - none of which the create form
 * edits. Each copied row loses its own id and audit fields, so it is created afresh.
 *
 * @param {Object} agm an AGM as the detail endpoint returns it
 * @returns {Object} an AGM without an id, not obsolete, carrying only the fields the create form edits
 */
export const buildDuplicateAgm = (agm) => {
	return {
		type: 'AffectedGenomicModel',
		taxon: structuredClone(agm.taxon) ?? { curie: '' },
		subtype: structuredClone(agm.subtype) ?? null,
		internal: agm.internal ?? false,
		obsolete: false,
		agmFullName: copyRow(agm.agmFullName),
		agmSynonyms: copyRows(agm.agmSynonyms),
		agmSecondaryIds: copyRows(agm.agmSecondaryIds),
	};
};

export const processErrors = (data, dispatch, agm) => {
	const errorMap = data?.supplementalData?.errorMap;
	const errorMessages = data?.errorMessages;

	// Dispatched before the row level messages, which are keyed off the agm and can fail on an
	// unexpected shape. These are the only messages some fields carry, so they go out first.
	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages: errorMessages || {},
	});

	processErrorMap(errorMap, dispatch, agm);
};

export const processErrorMap = (errorMap, dispatch, agm) => {
	if (!errorMap) return;

	Object.keys(errorMap).forEach((entityType) => {
		const tableErrors = errorMap[entityType];
		const table = agm[entityType];
		// Only an error map keyed by row or by field can be attached to a row. A plain string, or
		// an entity the agm does not carry, stays in the flat error messages.
		if (!table || typeof table !== 'object' || !tableErrors || typeof tableErrors !== 'object') {
			return;
		}
		processTableErrors(tableErrors, dispatch, entityType, table);
	});
};

export const processTableErrors = (tableErrors, dispatch, entityType, table) => {
	let errors = {};
	Object.keys(tableErrors).forEach((index) => {
		let row = Array.isArray(table) ? table[index] : table;
		let rowErrors = Array.isArray(table) ? tableErrors[index] : tableErrors;
		if (!row) return;
		errors[row.dataKey] = {};
		Object.keys(rowErrors).forEach((field) => {
			errors[row.dataKey][field] = {
				severity: 'error',
				message: rowErrors[field],
			};
		});
	});
	dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: entityType, errorMessages: errors });
};
