import { useMemo } from 'react';
import { AlleleSubResourceService } from '../../service/AlleleSubResourceService';
import { useEntitySubResource } from '../../hooks/useEntitySubResource';
import { ALLELE_SUB_RESOURCES } from './constants';

/**
 * One of an allele's lists, read and written through its own sub-resource rather than with the allele.
 *
 * @param {keyof typeof ALLELE_SUB_RESOURCES} type which list
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 * @returns {Object} what `useEntitySubResource` returns
 */
export const useAlleleSubResource = (type, alleleId) => {
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);
	return useEntitySubResource(ALLELE_SUB_RESOURCES[type], subResourceService, alleleId);
};
