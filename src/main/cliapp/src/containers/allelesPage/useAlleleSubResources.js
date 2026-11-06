import { ALLELE_SUB_RESOURCES } from './constants';
import { useAlleleSubResource } from './useAlleleSubResource';
import { subResourceSet } from '../../utils/subResourceSet';

/**
 * Every list in {@link ALLELE_SUB_RESOURCES} for one allele, for a page that saves, resets and tracks them
 * together.
 *
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 * @returns {{subResources: Object, saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function,
 *   carryOverAll: Function, dirtySteps: Array<{key: string, label: string}>}} the lists keyed as sections read them,
 *   a save of the edited ones, whether any is edited or being written, a reset of them all, a start over of them
 *   all from copies of their rows, and the edited lists as save progress rows
 */
export const useAlleleSubResources = (alleleId) => {
	const crossReferences = useAlleleSubResource('crossReferences', alleleId);
	const references = useAlleleSubResource('references', alleleId);
	const alleleGeneAssociations = useAlleleSubResource('alleleGeneAssociations', alleleId);
	const alleleSynonyms = useAlleleSubResource('alleleSynonyms', alleleId);
	const alleleSecondaryIds = useAlleleSubResource('alleleSecondaryIds', alleleId);
	const alleleNomenclatureEvents = useAlleleSubResource('alleleNomenclatureEvents', alleleId);
	const alleleMutationTypes = useAlleleSubResource('alleleMutationTypes', alleleId);
	const alleleFunctionalImpacts = useAlleleSubResource('alleleFunctionalImpacts', alleleId);
	const alleleInheritanceModes = useAlleleSubResource('alleleInheritanceModes', alleleId);
	const relatedNotes = useAlleleSubResource('relatedNotes', alleleId);

	const subResources = {
		crossReferences,
		references,
		alleleGeneAssociations,
		alleleSynonyms,
		alleleSecondaryIds,
		alleleNomenclatureEvents,
		alleleMutationTypes,
		alleleFunctionalImpacts,
		alleleInheritanceModes,
		relatedNotes,
	};

	/**
	 * Starts every list over with copies of the rows it shows, made as a duplicate's are, for the next allele to be
	 * created from the last one's rows. A list a duplicate does not copy, or whose section is hidden, starts over
	 * empty.
	 *
	 * @param {(fieldName: string) => boolean} isVisible whether the form shows a field's section
	 * @returns {Promise<void>} settled once every list holds its copies
	 */
	const carryOverAll = async (isVisible) => {
		await Promise.all(
			Object.entries(subResources).map(async ([type, subResource]) => {
				const { buildCopies, copyOnlyWhenVisible } = ALLELE_SUB_RESOURCES[type];
				const isCopied = buildCopies && (!copyOnlyWhenVisible || isVisible(copyOnlyWhenVisible));
				subResource.startOver(isCopied ? await buildCopies(subResource.rows) : []);
			})
		);
	};

	return { subResources, ...subResourceSet(subResources), carryOverAll };
};
