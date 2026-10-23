import { addDataKey } from '../../allelesPage/utils';
import { stripReference } from '../../allelesPage/referencesTable/utils';
import { stripUiFields as stripCrossReference } from '../../allelesPage/crossReferences/utils';

const ROW_FIELDS = ['transgenicToolSynonyms', 'transgenicToolUses', 'relatedNotes'];
const OBJECT_FIELDS = ['transgenicToolSymbol', 'transgenicToolFullName'];

const withoutDataKey = (row) => {
	if (!row) return row;
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, ...apiFields } = row;
	return apiFields;
};

/**
 * The tool as PUT /transgenic-tool takes it: the form's rows without the keys and helper fields the tables
 * add, references as the curies the API resolves, and without the associations summary, which the API
 * computes. The tool's associations are saved through their own endpoint.
 *
 * @param {Object} transgenicTool the tool as the form holds it
 * @returns {Object} a copy for the API; the argument is not modified
 */
export const buildTransgenicToolPayload = (transgenicTool) => {
	const payload = structuredClone(transgenicTool);
	delete payload.associatedTransgenicTools;

	OBJECT_FIELDS.forEach((field) => {
		payload[field] = withoutDataKey(payload[field]) ?? null;
	});
	ROW_FIELDS.forEach((field) => {
		payload[field] = (payload[field] ?? []).map(withoutDataKey);
	});
	payload.references = (payload.references ?? []).map(stripReference);
	payload.crossReferences = (payload.crossReferences ?? []).map(stripCrossReference);

	return payload;
};

/**
 * Readies the associations the API returned for the associations table: each gets its own `dataKey`.
 *
 * @param {Object[]} associations
 * @returns {Object[]} copies; the argument is not modified
 */
export const prepareToolAssociations = (associations) =>
	(associations ?? []).map((association) => {
		const row = structuredClone(association);
		addDataKey(row);
		return row;
	});

/**
 * An association as the replace endpoint takes it: its id, relation and other tool, and the audited fields.
 * The subject is the tool the list is saved for, so it is not sent, and the other tool goes by id.
 *
 * @param {Object} association
 * @returns {Object}
 */
export const stripToolAssociation = (association) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, transgenicToolAssociationSubject, transgenicToolTransgenicToolAssociationObject, ...fields } =
		association;
	const object = transgenicToolTransgenicToolAssociationObject;
	return {
		...fields,
		// A tool typed rather than picked is sent as no tool, so the API reports it missing. TransgenicTool is a
		// polymorphic entity, so the API needs its type to read it.
		transgenicToolTransgenicToolAssociationObject: object?.id ? { type: 'TransgenicTool', id: object.id } : null,
	};
};

const toolAssociationValue = (association) =>
	JSON.stringify([
		association.id ?? null,
		association.relation?.name ?? null,
		association.transgenicToolTransgenicToolAssociationObject?.id ??
			association.transgenicToolTransgenicToolAssociationObject?.curie ??
			null,
		Boolean(association.internal),
		Boolean(association.obsolete),
	]);

/**
 * Whether two association lists hold the same rows, in any order.
 *
 * @param {Object[]} associations
 * @param {Object[]} otherAssociations
 * @returns {boolean}
 */
export const haveSameToolAssociations = (associations, otherAssociations) => {
	if (associations.length !== otherAssociations.length) return false;
	const values = associations.map(toolAssociationValue).sort();
	const otherValues = otherAssociations.map(toolAssociationValue).sort();
	return values.every((value, index) => value === otherValues[index]);
};
