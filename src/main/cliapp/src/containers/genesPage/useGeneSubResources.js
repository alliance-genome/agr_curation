import { useMemo } from 'react';
import { GeneSubResourceService } from '../../service/GeneSubResourceService';
import { useEntitySubResource } from '../../hooks/useEntitySubResource';
import { subResourceSet } from '../../utils/subResourceSet';
import { GENE_SUB_RESOURCES } from './constants';

/**
 * Every list in {@link GENE_SUB_RESOURCES} for one gene, for the detail page, which saves, resets and tracks them
 * together.
 *
 * @param {number} [geneId] the gene to read on load
 * @returns {{subResources: Object, saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function,
 *   dirtySteps: Array<{key: string, label: string}>}} the lists keyed as sections read them, a save of the edited
 *   ones, whether any is edited or being written, a reset of them all, and the edited lists as save progress rows
 */
export const useGeneSubResources = (geneId) => {
	const subResourceService = useMemo(() => new GeneSubResourceService(), []);
	const geneSynonyms = useEntitySubResource(GENE_SUB_RESOURCES.geneSynonyms, subResourceService, geneId);
	const geneSecondaryIds = useEntitySubResource(GENE_SUB_RESOURCES.geneSecondaryIds, subResourceService, geneId);
	const relatedNotes = useEntitySubResource(GENE_SUB_RESOURCES.relatedNotes, subResourceService, geneId);
	const crossReferences = useEntitySubResource(GENE_SUB_RESOURCES.crossReferences, subResourceService, geneId);

	const subResources = { geneSynonyms, geneSecondaryIds, relatedNotes, crossReferences };

	return { subResources, ...subResourceSet(subResources) };
};
