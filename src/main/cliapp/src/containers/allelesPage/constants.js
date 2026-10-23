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
 * - `buildCopies` turns the rows a source allele holds, as the API returns them, into rows for a new allele
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
};
