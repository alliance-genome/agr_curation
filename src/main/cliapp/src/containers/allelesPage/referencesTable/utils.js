import { addDataKey, generateCrossRefSearchField, getShortCitation } from '../utils';

/**
 * Readies references for the references table: each gets its own `dataKey`, the cross reference
 * curies its filter matches on, and a `shortCitation` whichever shape it arrived in.
 *
 * @param {Object[]} references references as the API or the reference search returns them
 * @returns {Object[]} copies ready for the table; the argument is not modified
 */
export const prepareReferences = (references) =>
	(references ?? []).map((reference) => {
		const row = {
			...reference,
			shortCitation: getShortCitation(reference),
			crossReferencesFilter: generateCrossRefSearchField(reference),
		};
		addDataKey(row);
		return row;
	});

/**
 * A reference as the references sub-resource takes it, which reads only the curie.
 *
 * @param {Object} reference
 * @returns {{ type: string, curie: string }}
 */
export const stripReference = (reference) => ({ type: 'Reference', curie: reference.curie });

const sortedCuries = (references) => references.map((reference) => reference.curie).sort();

/**
 * Whether two lists hold the same references, by curie and in any order.
 *
 * @param {Object[]} references
 * @param {Object[]} otherReferences
 * @returns {boolean}
 */
export const haveSameReferences = (references, otherReferences) => {
	if (references.length !== otherReferences.length) return false;

	const otherCuries = sortedCuries(otherReferences);
	return sortedCuries(references).every((curie, index) => curie === otherCuries[index]);
};
