import { copyRow } from '../../utils/copyRows';
import { sortedCuries } from '../../utils/rows';
import { getPendingFields, nameSlotAnnotationValue } from '../../utils/pendingFields';

export const generateCrossRefSearchField = (reference) => {
	const { crossReferences, curieField } = getCrossReferences(reference);

	// The API leaves an empty list out, so a reference without cross references arrives without the key
	let refStrings = (crossReferences ?? []).map((crossRef) => crossRef[curieField]);

	return refStrings.join();
};

export const getCrossReferences = (reference) => {
	let crossReferences;
	let curieField;

	if (reference.cross_references) {
		crossReferences = structuredClone(reference.cross_references);
		curieField = 'curie';
	} else if (reference.crossReferences) {
		crossReferences = structuredClone(reference.crossReferences);
		curieField = 'referencedCurie';
	} else {
		return {};
	}

	return { crossReferences, curieField };
};

export const getShortCitation = (reference) => {
	let shortCitation;
	if (!reference.short_citation && !reference.shortCitation) return;

	if (reference.short_citation) {
		shortCitation = reference.short_citation;
	} else if (reference.shortCitation) {
		shortCitation = reference.shortCitation;
	}

	return shortCitation;
};

export const generateCurieSearchField = (entities) => {
	if (!entities) return;
	let curieStrings = entities.map((entity) => entity.curie);
	return curieStrings.join();
};

/**
 * Shapes a new allele for the create endpoint.
 *
 * Adds the `type` discriminator BiologicalEntity declares through `@JsonTypeInfo`, which an
 * allele loaded from the API already carries but a new one does not, and strips the placeholder
 * objects the initial state holds: the API reads `{ name: '' }` as a vocabulary term whose name
 * it cannot find and rejects it, and `{ curie: '' }` as a taxon it silently resolves to null.
 *
 * @param {Object} allele
 * @returns {Object} a copy carrying `type`, without a blank `taxon` or `inCollection`
 */
export const buildCreatePayload = (allele) => {
	const payload = structuredClone(allele);

	payload.type = 'Allele';

	if (!payload.taxon?.curie) {
		delete payload.taxon;
	}
	if (!payload.inCollection?.name) {
		delete payload.inCollection;
	}

	return payload;
};

/**
 * A new allele carrying the fields of a stored one that the create form edits, for the create page to
 * start from.
 *
 * Left behind: everything that identifies the stored allele (its ids), its data provider and audit fields,
 * and the variant and construct associations the form does not edit. The lists in ALLELE_SUB_RESOURCES are
 * written through their own sub-resources and copied separately; see useDuplicateAlleleSource. Each copied
 * slot annotation loses its own id and audit fields, so it is created afresh.
 *
 * @param {Object} allele an allele as the detail endpoint returns it
 * @returns {Object} an allele without an id, not obsolete
 */
export const buildDuplicateAllele = (allele) => ({
	type: 'Allele',
	taxon: structuredClone(allele.taxon) ?? { curie: '' },
	inCollection: structuredClone(allele.inCollection) ?? { name: '' },
	isExtinct: allele.isExtinct ?? false,
	internal: allele.internal ?? false,
	obsolete: false,
	alleleSymbol: copyRow(allele.alleleSymbol),
	alleleFullName: copyRow(allele.alleleFullName),
	alleleGermlineTransmissionStatus: copyRow(allele.alleleGermlineTransmissionStatus),
	alleleDatabaseStatus: copyRow(allele.alleleDatabaseStatus),
});

const statusSlotAnnotationValue = (statusSlotAnnotation, statusField) =>
	statusSlotAnnotation
		? JSON.stringify([
				statusSlotAnnotation[statusField]?.name ?? null,
				statusSlotAnnotation.internal ?? null,
				sortedCuries(statusSlotAnnotation.evidence),
			])
		: null;

// The value each single-value field and single-object section is compared by, with an unset or blank
// value read as none.
const SINGLE_VALUE_FIELD_VALUES = {
	taxon: (allele) => allele?.taxon?.curie || null,
	inCollection: (allele) => allele?.inCollection?.name || null,
	isExtinct: (allele) => allele?.isExtinct ?? null,
	internal: (allele) => allele?.internal ?? null,
	obsolete: (allele) => allele?.obsolete ?? null,
	alleleFullName: (allele) => nameSlotAnnotationValue(allele?.alleleFullName),
	alleleSymbol: (allele) => nameSlotAnnotationValue(allele?.alleleSymbol),
	alleleGermlineTransmissionStatus: (allele) =>
		statusSlotAnnotationValue(allele?.alleleGermlineTransmissionStatus, 'germlineTransmissionStatus'),
	alleleDatabaseStatus: (allele) => statusSlotAnnotationValue(allele?.alleleDatabaseStatus, 'databaseStatus'),
};

/**
 * The single-value fields and single-object sections whose value on the form differs from the allele as
 * last saved.
 *
 * @param {Object} allele the allele as the form holds it
 * @param {Object} [savedAllele] the allele as the API last returned it
 * @returns {Set<string>} the pending fields' names, none while there is no saved allele
 */
export const getPendingSingleValueFields = (allele, savedAllele) =>
	getPendingFields(allele, savedAllele, SINGLE_VALUE_FIELD_VALUES);
