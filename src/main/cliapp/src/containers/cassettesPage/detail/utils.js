import { stripReference } from '../../allelesPage/referencesTable/utils';
import { stripComponentRow } from '../components/utils';

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
