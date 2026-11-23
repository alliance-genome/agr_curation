import { useMemo } from 'react';
import { ConstructSubResourceService } from '../../service/ConstructSubResourceService';
import { useEntitySubResource } from '../../hooks/useEntitySubResource';
import { subResourceSet } from '../../utils/subResourceSet';
import { CONSTRUCT_SUB_RESOURCES } from './constants';

/**
 * Every list in {@link CONSTRUCT_SUB_RESOURCES} for one construct, for a page that saves, resets and tracks them
 * together.
 *
 * @param {number} [constructId] the construct to read on load
 * @returns {{subResources: Object, saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function,
 *   dirtySteps: Array<{key: string, label: string}>}} the lists keyed as sections read them, a save of the edited
 *   ones, whether any is edited or being written, a reset of them all, and the edited lists as save progress rows
 */
export const useConstructSubResources = (constructId) => {
	const subResourceService = useMemo(() => new ConstructSubResourceService(), []);
	const constructSynonyms = useEntitySubResource(
		CONSTRUCT_SUB_RESOURCES.constructSynonyms,
		subResourceService,
		constructId
	);
	const secondaryIdentifiers = useEntitySubResource(
		CONSTRUCT_SUB_RESOURCES.secondaryIdentifiers,
		subResourceService,
		constructId
	);
	const constructCassetteAssociations = useEntitySubResource(
		CONSTRUCT_SUB_RESOURCES.constructCassetteAssociations,
		subResourceService,
		constructId
	);
	const relatedNotes = useEntitySubResource(CONSTRUCT_SUB_RESOURCES.relatedNotes, subResourceService, constructId);
	const references = useEntitySubResource(CONSTRUCT_SUB_RESOURCES.references, subResourceService, constructId);

	const subResources = {
		constructSynonyms,
		secondaryIdentifiers,
		constructCassetteAssociations,
		relatedNotes,
		references,
	};

	return { subResources, ...subResourceSet(subResources) };
};
