import { ALLELE_SUB_RESOURCES } from './constants';
import { useAlleleSubResource } from './useAlleleSubResource';

/**
 * Every list in {@link ALLELE_SUB_RESOURCES} for one allele, for a page that saves, resets and tracks them
 * together.
 *
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 * @returns {{subResources: Object, saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function}}
 *   the lists keyed as sections read them, a save of the edited ones, whether any is edited or being written,
 *   and a reset of them all
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
	const lists = Object.entries(subResources);

	/**
	 * Writes each list that has been edited, one after another, each as its whole list. A list that has not been
	 * edited is not written, so its stored rows stay as they are, and the marks a past save or a note edit left
	 * on its rows are cleared.
	 *
	 * @param {number} [targetAlleleId] the allele to write to, for create, which has none on load
	 * @returns {Promise<Array<{label: string, outcome: {isSuccess: boolean, message?: string}}>>} the outcome of
	 *   each list written, in registry order
	 */
	const saveDirty = async (targetAlleleId) => {
		const outcomes = [];
		for (const [type, subResource] of lists) {
			if (subResource.isDirty) {
				outcomes.push({ label: ALLELE_SUB_RESOURCES[type].label, outcome: await subResource.save(targetAlleleId) });
			} else {
				subResource.setErrorMessages({});
			}
		}
		return outcomes;
	};

	const resetAll = () => lists.forEach(([, subResource]) => subResource.reset());

	return {
		subResources,
		isDirty: lists.some(([, subResource]) => subResource.isDirty),
		isSaving: lists.some(([, subResource]) => subResource.isSaving),
		saveDirty,
		resetAll,
	};
};
