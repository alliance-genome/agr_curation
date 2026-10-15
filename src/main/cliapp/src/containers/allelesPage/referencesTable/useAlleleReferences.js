import { useCallback, useMemo } from 'react';
import { ReferenceService } from '../../../service/ReferenceService';
import { useSubResourceList } from '../../../hooks/useSubResourceList';
import { haveSameReferences, prepareReferences, stripReference } from './utils';

/**
 * An allele's references, read and written through their own sub-resource rather than with the allele.
 * The allele reducer never holds them, so the allele payload never carries them and the detail
 * endpoints cannot clear them.
 *
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 */
export const useAlleleReferences = (alleleId) => {
	const referenceService = useMemo(() => new ReferenceService(), []);
	const load = useCallback(
		(targetAlleleId) => referenceService.getReferencesForAllele(targetAlleleId),
		[referenceService]
	);
	const replace = useCallback(
		(targetAlleleId, references) => referenceService.replaceReferencesForAllele(targetAlleleId, references),
		[referenceService]
	);

	const { rows, setRows, ...subResource } = useSubResourceList({
		ownerId: alleleId,
		label: 'references',
		errorField: 'references',
		load,
		replace,
		prepare: prepareReferences,
		strip: stripReference,
		isSame: haveSameReferences,
	});

	return { references: rows, setReferences: setRows, ...subResource };
};
