import { slotAnnotationList, synonymFields } from '../../utils/slotAnnotations';
import { addDataKey, sameRowsBy } from '../../utils/rows';
import { haveSameRelatedNotes, prepareRelatedNotes, stripRelatedNote } from '../../components/relatedNotes/utils';
import { haveSameReferences, prepareReferences, stripReference } from '../allelesPage/referencesTable/utils';

/**
 * A construct's secondary IDs as table rows: they are plain strings on the construct, so each gets a row of its own
 * holding it as `secondaryId`.
 *
 * @param {string[]} [secondaryIds] secondary IDs as the API returns them
 * @returns {Object[]} rows ready for the table
 */
export const prepareSecondaryIdentifiers = (secondaryIds) =>
	(secondaryIds ?? []).map((secondaryId) => {
		const row = { secondaryId };
		addDataKey(row);
		return row;
	});

/**
 * A construct's cassette associations, each with its own `dataKey`.
 *
 * @param {Object[]} [associations] cassette associations as the API returns them
 * @returns {Object[]} copies ready for the table; the argument is not modified
 */
export const prepareCassetteAssociations = (associations) =>
	(associations ?? []).map((association) => {
		const row = { ...association };
		addDataKey(row);
		return row;
	});

/**
 * A cassette association as its sub-resource takes it, without the `dataKey` the table keeps for itself.
 *
 * @param {Object} association
 * @returns {Object} a copy without `dataKey`
 */
export const stripCassetteAssociation = (association) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, ...rest } = association;
	return rest;
};

/**
 * Whether two lists hold the same cassette associations, by id, cassette and relation, and in any order.
 *
 * @param {Object[]} associations
 * @param {Object[]} otherAssociations
 * @returns {boolean}
 */
export const haveSameCassetteAssociations = (associations, otherAssociations) => {
	const keysOf = (rows) =>
		rows
			.map((row) =>
				JSON.stringify([row.id ?? null, row.constructCassetteAssociationObject?.id ?? null, row.relation?.name ?? null])
			)
			.sort();
	const otherKeys = keysOf(otherAssociations);
	return (
		associations.length === otherAssociations.length && keysOf(associations).every((key, ix) => key === otherKeys[ix])
	);
};

/**
 * The lists a construct holds through sub-resources of its own rather than in its payload, keyed by the name the
 * detail page provides each under, in the order the page saves them. Each entry configures `useEntitySubResource`.
 */
export const CONSTRUCT_SUB_RESOURCES = {
	constructSynonyms: slotAnnotationList({
		path: 'synonyms',
		label: 'synonyms',
		errorField: 'constructSynonyms',
		fieldsOf: synonymFields,
	}),
	secondaryIdentifiers: {
		path: 'secondaryids',
		label: 'secondary IDs',
		errorField: 'secondaryIdentifiers',
		prepare: prepareSecondaryIdentifiers,
		strip: (row) => row.secondaryId,
		isSame: sameRowsBy((row) => row.secondaryId || ''),
	},
	constructCassetteAssociations: {
		path: 'cassetteassociations',
		label: 'cassettes',
		errorField: 'constructCassetteAssociations',
		prepare: prepareCassetteAssociations,
		strip: stripCassetteAssociation,
		isSame: haveSameCassetteAssociations,
	},
	relatedNotes: {
		path: 'relatednotes',
		label: 'related notes',
		errorField: 'relatedNotes',
		prepare: prepareRelatedNotes,
		strip: stripRelatedNote,
		isSame: haveSameRelatedNotes,
	},
	references: {
		path: 'references',
		label: 'references',
		errorField: 'references',
		prepare: prepareReferences,
		strip: stripReference,
		isSame: haveSameReferences,
	},
};
