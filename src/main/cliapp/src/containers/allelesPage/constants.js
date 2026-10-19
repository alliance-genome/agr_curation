import { haveSameCrossReferences, prepareCrossReferences, stripUiFields } from './crossReferences/utils';
import { haveSameReferences, prepareReferences, stripReference } from './referencesTable/utils';
import {
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
 * - `path` the list's path segment under the allele, such as `cross-references`
 * - `label` what the rows are, for messages
 * - `errorField` the field the API keys the rows' errors under
 * - `prepare`, `strip` and `isSame` as `useSubResourceList` takes them
 * - `markUnsavableRows`, optional: marks rows that cannot be saved as they are, returning the rows' errors
 *   with them marked, or null when there are none; while it marks any, a save is refused with
 *   `unsavableRowsMessage`
 */
export const ALLELE_SUB_RESOURCES = {
	crossReferences: {
		path: 'cross-references',
		label: 'cross references',
		errorField: 'crossReferences',
		prepare: prepareCrossReferences,
		strip: stripUiFields,
		isSame: haveSameCrossReferences,
	},
	references: {
		path: 'references',
		label: 'references',
		errorField: 'references',
		prepare: prepareReferences,
		strip: stripReference,
		isSame: haveSameReferences,
	},
	alleleGeneAssociations: {
		path: 'gene-associations',
		label: 'gene associations',
		errorField: 'alleleGeneAssociations',
		prepare: prepareGeneAssociations,
		strip: stripGeneAssociation,
		isSame: haveSameGeneAssociations,
		markUnsavableRows: markRowsMissingGene,
		unsavableRowsMessage: 'Select each gene from the autosuggest',
	},
};
