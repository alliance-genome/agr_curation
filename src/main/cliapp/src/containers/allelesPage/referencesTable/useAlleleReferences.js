import { useCallback, useMemo } from 'react';
import { AlleleSubResourceService } from '../../../service/AlleleSubResourceService';
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
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);
	const load = useCallback(
		(targetAlleleId) => subResourceService.getList(targetAlleleId, 'references'),
		[subResourceService]
	);
	const replace = useCallback(
		(targetAlleleId, references) => subResourceService.replaceList(targetAlleleId, 'references', references),
		[subResourceService]
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
