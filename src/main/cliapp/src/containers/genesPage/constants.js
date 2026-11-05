import { slotAnnotationList, synonymFields, secondaryIdFields } from '../../utils/slotAnnotations';
import {
	buildCrossReferenceCopies,
	haveSameCrossReferences,
	prepareCrossReferences,
	seedResourceDescriptors,
	stripUiFields,
} from '../../components/crossReferences/utils';
import {
	buildRelatedNoteCopies,
	haveSameRelatedNotes,
	prepareRelatedNotes,
	stripRelatedNote,
} from '../../components/relatedNotes/utils';

/**
 * The lists a gene holds through sub-resources of its own rather than in its payload, keyed by the name the detail
 * page provides each under, in the order the page saves them. Each entry configures `useEntitySubResource`, and
 * optionally carries:
 *
 * - `buildCopies`: turns the rows a gene holds, as the API returns them or as the table holds them, into rows for a
 *   new gene; a list without it is not copied
 * - `copyOnlyWhenVisible`: the form field whose section must be shown for the rows to be copied
 */
export const GENE_SUB_RESOURCES = {
	geneSynonyms: slotAnnotationList({
		path: 'synonyms',
		label: 'synonyms',
		errorField: 'geneSynonyms',
		fieldsOf: synonymFields,
	}),
	geneSecondaryIds: slotAnnotationList({
		path: 'secondaryids',
		label: 'secondary IDs',
		errorField: 'geneSecondaryIds',
		fieldsOf: secondaryIdFields,
		copy: false,
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
	crossReferences: {
		path: 'crossreferences',
		label: 'cross references',
		errorField: 'crossReferences',
		prepare: prepareCrossReferences,
		strip: stripUiFields,
		isSame: haveSameCrossReferences,
		// The copies keep the source's pages but not its curies, which the curator fills in for the new gene. A hidden
		// section could not show them, so it gets none.
		buildCopies: async (crossReferences) => seedResourceDescriptors(buildCrossReferenceCopies(crossReferences)),
		copyOnlyWhenVisible: 'Cross References',
	},
};
