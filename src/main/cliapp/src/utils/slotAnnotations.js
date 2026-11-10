import { addDataKey, sameRowsBy, sortedCuries } from './rows';
import { copyRow } from './copyRows';

/**
 * Readies an entity's slot annotations for their table: each gets its own `dataKey`.
 *
 * @param {Object[]} [annotations] slot annotations as the API returns them
 * @returns {Object[]} copies ready for the table; the argument is not modified
 */
export const prepareSlotAnnotations = (annotations) =>
	(annotations ?? []).map((annotation) => {
		const row = { ...annotation };
		addDataKey(row);
		return row;
	});

/**
 * A slot annotation as its sub-resource takes it, without the `dataKey` the table keeps for itself. The API
 * assigns each annotation's allele, so none is sent.
 *
 * @param {Object} annotation
 * @returns {Object} a copy without `dataKey` or `singleAllele`
 */
export const stripSlotAnnotation = (annotation) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, singleAllele, ...rest } = annotation;
	return rest;
};

/**
 * Copies of an entity's slot annotations for a new entity. Each copy loses its id and audit fields, so it is
 * created afresh.
 *
 * @param {Object[]} [annotations] slot annotations as the API returns them or as the table holds them
 * @returns {Object[]} rows ready for the table, each with its own `dataKey`
 */
export const buildSlotAnnotationCopies = (annotations) => prepareSlotAnnotations((annotations ?? []).map(copyRow));

/**
 * @param {(annotation: Object) => Array} fieldsOf the curator-editable fields of one list's annotations, other
 *   than those every slot annotation has
 * @returns {(annotations: Object[], otherAnnotations: Object[]) => boolean} whether two lists hold the same
 *   annotations, row by row: the same annotation with the same internal flag, evidence and fields
 */
export const haveSameSlotAnnotationsBy = (fieldsOf) =>
	sameRowsBy((annotation) =>
		JSON.stringify([
			annotation.id ?? null,
			Boolean(annotation.internal),
			sortedCuries(annotation.evidence),
			...fieldsOf(annotation),
		])
	);

/**
 * A registry entry for one of an entity's slot annotation lists.
 *
 * @param {Object} options
 * @param {string} options.path the list's path segment under the entity
 * @param {string} options.label what the rows are, for messages
 * @param {string} options.errorField the field the API keys the rows' errors under
 * @param {(annotation: Object) => Array} options.fieldsOf the curator-editable fields of the list's own, compared
 *   along with those every slot annotation has
 */
export const slotAnnotationList = ({ path, label, errorField, fieldsOf }) => ({
	path,
	label,
	errorField,
	prepare: prepareSlotAnnotations,
	strip: stripSlotAnnotation,
	isSame: haveSameSlotAnnotationsBy(fieldsOf),
	buildCopies: buildSlotAnnotationCopies,
});

/**
 * @param {Object} synonym
 * @returns {Array} a synonym's own curator-editable fields, for comparing synonym lists
 */
export const synonymFields = (synonym) => [
	synonym.displayText || null,
	synonym.formatText || null,
	synonym.synonymUrl || null,
	synonym.nameType?.name ?? null,
	synonym.synonymScope?.name ?? null,
];

/**
 * @param {Object} secondaryId
 * @returns {Array} a secondary ID's own curator-editable fields, for comparing secondary ID lists
 */
export const secondaryIdFields = (secondaryId) => [secondaryId.secondaryId || null];
