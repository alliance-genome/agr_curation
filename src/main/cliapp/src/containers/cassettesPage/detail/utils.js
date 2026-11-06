import { stripReference } from '../../allelesPage/referencesTable/utils';
import { copyRow, copyRows } from '../../../utils/copyRows';
import { prepareComponentRows, stripComponentRow } from '../components/utils';

const ROW_FIELDS = ['cassetteSynonyms', 'cassetteUses'];
const OBJECT_FIELDS = ['cassetteSymbol', 'cassetteFullName'];
const COMPONENT_FIELDS = [
	'cassetteComponents',
	'cassetteGenomicEntityAssociations',
	'cassetteTransgenicToolAssociations',
	'cassetteStrAssociations',
];

const withoutDataKey = (row) => {
	if (!row) return row;
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, ...apiFields } = row;
	return apiFields;
};

/**
 * The cassette as PUT /cassette takes it: the form's rows without the keys the tables add, and references as the
 * curies the API resolves. The component lists are left out: the update keeps the stored ones, and each is saved
 * through its own endpoint. Related notes, which the page does not show, go back as they were read, as the update
 * replaces them with what it is sent.
 *
 * @param {Object} cassette the cassette as the form holds it
 * @returns {Object} a copy for the API; the argument is not modified
 */
export const buildCassettePayload = (cassette) => {
	const payload = structuredClone(cassette);
	COMPONENT_FIELDS.forEach((field) => delete payload[field]);

	OBJECT_FIELDS.forEach((field) => {
		payload[field] = withoutDataKey(payload[field]) ?? null;
	});
	ROW_FIELDS.forEach((field) => {
		payload[field] = (payload[field] ?? []).map(withoutDataKey);
	});
	payload.references = (payload.references ?? []).map(stripReference);

	return payload;
};

/**
 * The cassette as POST /cassette takes it (SCRUM-6748): as {@link buildCassettePayload} leaves it, with the type
 * Reagent's JSON type info needs to read it. The component lists are saved through their own endpoints once the
 * cassette exists. The data provider is left to the API, which gives a new cassette the curator's MOD.
 *
 * @param {Object} cassette the cassette as the create form holds it
 * @returns {Object} a copy for the API; the argument is not modified
 */
export const buildCassetteCreatePayload = (cassette) => ({ ...buildCassettePayload(cassette), type: 'Cassette' });

// The cassette's fields a new cassette starts with
export const NEW_CASSETTE = { type: 'Cassette', placeholder: false, internal: false, obsolete: false };

/**
 * A new cassette carrying the fields of a stored one that the create form edits, for Clone and for Save & Add
 * Another to start from (SCRUM-6748).
 *
 * Left behind: everything that identifies the stored cassette (its ids, and its secondary IDs, which name it alone),
 * its data provider, related notes and audit fields, which the create form does not edit, and its component lists,
 * which the create page holds apart ({@link copyComponentRows}). Each copied row loses its own id and audit fields,
 * so it is created afresh; the terms and references a row points to are kept.
 *
 * @param {Object} cassette a cassette as the API returns it
 * @returns {Object} a cassette without an id, not obsolete, carrying only the fields the create form edits
 */
export const buildDuplicateCassette = (cassette) => ({
	...NEW_CASSETTE,
	placeholder: cassette.placeholder ?? false,
	internal: cassette.internal ?? false,
	cassetteSymbol: copyRow(cassette.cassetteSymbol),
	cassetteFullName: copyRow(cassette.cassetteFullName),
	cassetteSynonyms: copyRows(cassette.cassetteSynonyms) ?? [],
	cassetteUses: copyRows(cassette.cassetteUses) ?? [],
	references: structuredClone(cassette.references ?? []),
});

/**
 * A component list's rows as new rows for another cassette: each without its id, audit fields and cassette, its
 * related notes copied the same way, and ready for the list's table.
 *
 * @param {Object[]} rows the rows as the list holds them
 * @returns {Object[]} copies; the argument is not modified
 */
export const copyComponentRows = (rows) =>
	prepareComponentRows(
		(rows ?? []).map((row) => {
			/* eslint-disable-next-line no-unused-vars */
			const { dataKey, ...copy } = stripComponentRow(copyRow(row));
			copy.relatedNotes = (copy.relatedNotes ?? []).map(copyRow);
			return copy;
		})
	);

/**
 * Whether two lists of component rows hold the same rows in the same order, compared as the replace endpoint would
 * receive them.
 *
 * @param {Object[]} rows
 * @param {Object[]} otherRows
 * @returns {boolean}
 */
export const haveSameComponentRows = (rows, otherRows) =>
	rows.length === otherRows.length &&
	JSON.stringify(rows.map(stripComponentRow)) === JSON.stringify(otherRows.map(stripComponentRow));
