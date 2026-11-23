import { useMemo } from 'react';
import { GeneSubResourceService } from '../../service/GeneSubResourceService';
import { useEntitySubResource } from '../../hooks/useEntitySubResource';
import { subResourceSet } from '../../utils/subResourceSet';
import { GENE_SUB_RESOURCES } from './constants';

/**
 * Every list in {@link GENE_SUB_RESOURCES} for one gene, for a page that saves, resets and tracks them together.
 *
 * @param {number} [geneId] the gene to read on load. Create passes none and saves once it has one.
 * @returns {{subResources: Object, saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function,
 *   carryOverAll: Function, dirtySteps: Array<{key: string, label: string}>}} the lists keyed as sections read them,
 *   a save of the edited ones, whether any is edited or being written, a reset of them all, a start over of them all
 *   from copies of their rows, and the edited lists as save progress rows
 */
export const useGeneSubResources = (geneId) => {
	const subResourceService = useMemo(() => new GeneSubResourceService(), []);
	const geneSynonyms = useEntitySubResource(GENE_SUB_RESOURCES.geneSynonyms, subResourceService, geneId);
	const geneSecondaryIds = useEntitySubResource(GENE_SUB_RESOURCES.geneSecondaryIds, subResourceService, geneId);
	const relatedNotes = useEntitySubResource(GENE_SUB_RESOURCES.relatedNotes, subResourceService, geneId);
	const crossReferences = useEntitySubResource(GENE_SUB_RESOURCES.crossReferences, subResourceService, geneId);

	const subResources = { geneSynonyms, geneSecondaryIds, relatedNotes, crossReferences };

	/**
	 * Starts every list over with copies of the rows it shows, for the next gene to be created from the last one's
	 * rows. A list that is not copied starts over empty, as does a list copied only while its section is shown when
	 * that section is hidden.
	 *
	 * @param {(fieldName: string) => boolean} isVisible whether the form shows a field's section
	 * @returns {Promise<void>} settled once every list holds its copies
	 */
	const carryOverAll = async (isVisible) => {
		await Promise.all(
			Object.entries(subResources).map(async ([type, subResource]) => {
				const { buildCopies, copyOnlyWhenVisible } = GENE_SUB_RESOURCES[type];
				const isCopied = buildCopies && (!copyOnlyWhenVisible || isVisible(copyOnlyWhenVisible));
				subResource.startOver(isCopied ? await buildCopies(subResource.rows) : []);
			})
		);
	};

	return { subResources, ...subResourceSet(subResources), carryOverAll };
};
