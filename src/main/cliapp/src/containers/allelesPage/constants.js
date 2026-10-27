import {
	buildDuplicateCrossReferences,
	haveSameCrossReferences,
	prepareCrossReferences,
	seedResourceDescriptors,
	stripUiFields,
} from './crossReferences/utils';
import { haveSameReferences, prepareReferences, stripReference } from './referencesTable/utils';
import {
	buildDuplicateGeneAssociations,
	haveSameGeneAssociations,
	markRowsMissingGene,
	prepareGeneAssociations,
	stripGeneAssociation,
} from './alleleGeneAssociations/utils';
import {
	buildSlotAnnotationCopies,
	haveSameSlotAnnotationsBy,
	prepareSlotAnnotations,
	stripSlotAnnotation,
} from './slotAnnotations/utils';
import {
	buildRelatedNoteCopies,
	haveSameRelatedNotes,
	prepareRelatedNotes,
	stripRelatedNote,
} from './relatedNotes/utils';
import { sortedCuries } from './utils';

/**
 * A registry entry for one of the allele's slot annotation lists.
 *
 * @param {Object} options
 * @param {string} options.path the list's path segment under the allele
 * @param {string} options.label what the rows are, for messages
 * @param {string} options.errorField the field the API keys the rows' errors under
 * @param {(annotation: Object) => Array} options.fieldsOf the curator-editable fields of the list's own, compared
 *   along with those every slot annotation has
 * @param {boolean} [options.copy] whether a new allele copies the list
 */
const slotAnnotationList = ({ path, label, errorField, fieldsOf, copy = true }) => ({
	path,
	label,
	errorField,
	prepare: prepareSlotAnnotations,
	strip: stripSlotAnnotation,
	isSame: haveSameSlotAnnotationsBy(fieldsOf),
	...(copy && { buildCopies: buildSlotAnnotationCopies }),
});

/**
 * The lists an allele holds through sub-resources of its own rather than in its payload, keyed by the name
 * the pages provide each under. The allele reducer never holds them, so the allele payload never carries them
 * and the detail endpoints cannot change them.
 *
 * Each entry configures `useAlleleSubResource`:
 * - `path` the list's path segment under the allele, such as `crossreferences`
 * - `label` what the rows are, for messages
 * - `errorField` the field the API keys the rows' errors under
 * - `prepare`, `strip` and `isSame` as `useSubResourceList` takes them
 * - `markUnsavableRows`, optional: marks rows that cannot be saved as they are, returning the rows' errors
 *   with them marked, or null when there are none; while it marks any, a save is refused with
 *   `unsavableRowsMessage`
 *
 * and `useDuplicateAlleleSource`:
 * - `buildCopies`, optional: turns the rows a source allele holds, as the API returns them, into rows for a new
 *   allele; a list without it is not copied
 * - `copyOnlyWhenVisible`, optional: the form field whose section must be shown for the rows to be copied
 */
export const ALLELE_SUB_RESOURCES = {
	crossReferences: {
		path: 'crossreferences',
		label: 'cross references',
		errorField: 'crossReferences',
		prepare: prepareCrossReferences,
		strip: stripUiFields,
		isSame: haveSameCrossReferences,
		// The copies keep the source's pages but not its curies, which the curator fills in for the new allele.
		// A hidden section could not show them, so it gets none.
		buildCopies: async (crossReferences) => seedResourceDescriptors(buildDuplicateCrossReferences(crossReferences)),
		copyOnlyWhenVisible: 'Cross References',
	},
	references: {
		path: 'references',
		label: 'references',
		errorField: 'references',
		prepare: prepareReferences,
		strip: stripReference,
		isSame: haveSameReferences,
		buildCopies: prepareReferences,
	},
	alleleGeneAssociations: {
		path: 'geneassociations',
		label: 'gene associations',
		errorField: 'alleleGeneAssociations',
		prepare: prepareGeneAssociations,
		strip: stripGeneAssociation,
		isSame: haveSameGeneAssociations,
		buildCopies: buildDuplicateGeneAssociations,
		markUnsavableRows: markRowsMissingGene,
		unsavableRowsMessage: 'Select each gene from the autosuggest',
	},
	alleleSynonyms: slotAnnotationList({
		path: 'synonyms',
		label: 'synonyms',
		errorField: 'alleleSynonyms',
		fieldsOf: (synonym) => [
			synonym.displayText || null,
			synonym.formatText || null,
			synonym.synonymUrl || null,
			synonym.nameType?.name ?? null,
			synonym.synonymScope?.name ?? null,
		],
	}),
	// Secondary IDs identify the allele they belong to, so a new allele copies none.
	alleleSecondaryIds: slotAnnotationList({
		path: 'secondaryids',
		label: 'secondary IDs',
		errorField: 'alleleSecondaryIds',
		fieldsOf: (secondaryId) => [secondaryId.secondaryId || null],
		copy: false,
	}),
	alleleNomenclatureEvents: slotAnnotationList({
		path: 'nomenclatureevents',
		label: 'nomenclature events',
		errorField: 'alleleNomenclatureEvents',
		fieldsOf: (event) => [event.nomenclatureEvent?.name ?? null, Boolean(event.obsolete)],
	}),
	alleleMutationTypes: slotAnnotationList({
		path: 'mutationtypes',
		label: 'mutation types',
		errorField: 'alleleMutationTypes',
		fieldsOf: (mutationType) => [sortedCuries(mutationType.mutationTypes)],
	}),
	alleleFunctionalImpacts: slotAnnotationList({
		path: 'functionalimpacts',
		label: 'functional impacts',
		errorField: 'alleleFunctionalImpacts',
		fieldsOf: (functionalImpact) => [
			(functionalImpact.functionalImpacts ?? []).map((term) => term?.name).sort(),
			functionalImpact.phenotypeTerm?.curie ?? null,
			functionalImpact.phenotypeStatement || null,
		],
	}),
	alleleInheritanceModes: slotAnnotationList({
		path: 'inheritancemodes',
		label: 'inheritance modes',
		errorField: 'alleleInheritanceModes',
		fieldsOf: (inheritanceMode) => [
			inheritanceMode.inheritanceMode?.name ?? null,
			inheritanceMode.phenotypeTerm?.curie ?? null,
			inheritanceMode.phenotypeStatement || null,
		],
	}),
	relatedNotes: {
		path: 'relatednotes',
		label: 'related notes',
		errorField: 'relatedNotes',
		prepare: prepareRelatedNotes,
		strip: stripRelatedNote,
		isSame: haveSameRelatedNotes,
		buildCopies: buildRelatedNoteCopies,
	},
};
