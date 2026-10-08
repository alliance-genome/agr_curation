import { useCallback, useMemo } from 'react';
import { CrossReferenceService } from '../../../service/CrossReferenceService';
import { useSubResourceList } from '../../../hooks/useSubResourceList';
import { addDataKey } from '../utils';
import { haveSameCrossReferences, seedResourceDescriptors, stripUiFields } from './utils';

const keyAndSeed = async (crossReferences) => {
	crossReferences.forEach(addDataKey);
	return seedResourceDescriptors(crossReferences);
};

/**
 * An allele's cross references, read and written through their own sub-resource rather than with the
 * allele. The allele reducer never holds them, so the allele payload never carries them and the
 * detail endpoints cannot clear them.
 *
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 */
export const useAlleleCrossReferences = (alleleId) => {
	const crossReferenceService = useMemo(() => new CrossReferenceService(), []);
	const load = useCallback(
		(targetAlleleId) => crossReferenceService.getCrossReferencesForAllele(targetAlleleId),
		[crossReferenceService]
	);
	const replace = useCallback(
		(targetAlleleId, crossReferences) =>
			crossReferenceService.replaceCrossReferencesForAllele(targetAlleleId, crossReferences),
		[crossReferenceService]
	);

	const { rows, setRows, ...subResource } = useSubResourceList({
		ownerId: alleleId,
		label: 'cross references',
		errorField: 'crossReferences',
		load,
		replace,
		prepare: keyAndSeed,
		strip: stripUiFields,
		isSame: haveSameCrossReferences,
	});

	return { crossReferences: rows, setCrossReferences: setRows, ...subResource };
};
