import { addDataKey, copyRow, generateCurieSearchField, sameRowsBy, sortedCuries } from '../utils';

/**
 * Readies gene associations for the gene associations table: each gets its own `dataKey` and the evidence
 * curies its filter matches on.
 *
 * @param {Object[]} associations gene associations as the API returns them
 * @returns {Object[]} copies ready for the table; the argument is not modified
 */
export const prepareGeneAssociations = (associations) =>
	(associations ?? []).map((association) => {
		const row = {
			...association,
			evidenceCurieSearchFilter: generateCurieSearchField(association.evidence),
		};
		addDataKey(row);
		return row;
	});

/**
 * A gene association as the gene associations sub-resource takes it, without the fields the table keeps
 * for itself. The API assigns each association's subject, so none is sent.
 *
 * @param {Object} association
 * @returns {Object} a copy without `dataKey`, `evidenceCurieSearchFilter` or `alleleAssociationSubject`
 */
export const stripGeneAssociation = (association) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, evidenceCurieSearchFilter, alleleAssociationSubject, ...rest } = association;
	return rest;
};

// Text typed into an autosuggest is held as an object carrying only the typed text. A typed gene has
// no id, so it compares unequal to every picked gene; a typed evidence code compares by its curie.
const autosuggestValue = (value, key) => {
	if (!value) return null;
	return value[key] ?? `typed:${JSON.stringify(value)}`;
};

const relatedNoteValue = (note) =>
	note
		? [note.freeText ?? null, note.noteType?.name ?? null, Boolean(note.internal), sortedCuries(note.references)]
		: null;

// A gene association's curator-editable fields, as a string to compare by.
const geneAssociationValue = (association) =>
	JSON.stringify([
		association.id ?? null,
		association.relation?.name ?? null,
		autosuggestValue(association.alleleGeneAssociationObject, 'id'),
		autosuggestValue(association.evidenceCode, 'curie'),
		sortedCuries(association.evidence),
		relatedNoteValue(association.relatedNote),
	]);

/**
 * Whether two lists hold the same gene associations, row by row: the same association with the same
 * relation, gene, evidence code, evidence and note.
 *
 * @param {Object[]} associations
 * @param {Object[]} otherAssociations
 * @returns {boolean}
 */
export const haveSameGeneAssociations = sameRowsBy(geneAssociationValue);

/**
 * Copies of an allele's gene associations for a new allele. Each copy, and its note, loses its id and audit
 * fields, so it is created afresh, and the subject, which the API assigns.
 *
 * @param {Object[]} [associations] gene associations as the API returns them
 * @returns {Object[]} rows ready for the table, each with its own `dataKey`
 */
export const buildDuplicateGeneAssociations = (associations) =>
	prepareGeneAssociations(
		(associations ?? []).map((association) => {
			const copy = copyRow(association);
			delete copy.alleleAssociationSubject;
			if (copy.relatedNote) {
				copy.relatedNote = copyRow(copy.relatedNote);
			}
			return copy;
		})
	);

/**
 * Marks each row whose gene has no id: typed rather than picked from the autosuggest, or not given at
 * all.
 *
 * @param {Object[]} associations
 * @param {Object} errorMessages the rows' current errors, keyed by `dataKey`
 * @returns {Object|null} the errors with those rows marked, or null when every row has a gene
 */
export const markRowsMissingGene = (associations, errorMessages) => {
	const fieldName = 'alleleGeneAssociationObject';
	const missing = associations.filter((association) => {
		return !association[fieldName]?.id;
	});
	if (missing.length === 0) return null;

	const marked = structuredClone(errorMessages);
	missing.forEach((association) => {
		marked[association.dataKey] = {
			...marked[association.dataKey],
			[fieldName]: { message: 'Must select Gene from autosuggest', severity: 'error' },
		};
	});
	return marked;
};
