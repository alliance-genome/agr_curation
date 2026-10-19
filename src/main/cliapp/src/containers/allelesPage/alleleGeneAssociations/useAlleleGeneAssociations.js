import { useCallback, useMemo } from 'react';
import { AlleleSubResourceService } from '../../../service/AlleleSubResourceService';
import { useSubResourceList } from '../../../hooks/useSubResourceList';
import { haveSameGeneAssociations, markRowsMissingGene, prepareGeneAssociations, stripGeneAssociation } from './utils';

/**
 * An allele's gene associations, read and written through their own sub-resource rather than with the allele.
 * The allele reducer never holds them, so the allele payload never carries them and the detail endpoints
 * cannot change them.
 *
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 */
export const useAlleleGeneAssociations = (alleleId) => {
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);
	const load = useCallback(
		(targetAlleleId) => subResourceService.getList(targetAlleleId, 'gene-associations'),
		[subResourceService]
	);
	const replace = useCallback(
		(targetAlleleId, associations) => subResourceService.replaceList(targetAlleleId, 'gene-associations', associations),
		[subResourceService]
	);

	const { rows, setRows, save, errorMessages, setErrorMessages, isLoading, loadError, ...subResource } =
		useSubResourceList({
			ownerId: alleleId,
			label: 'gene associations',
			errorField: 'alleleGeneAssociations',
			load,
			replace,
			prepare: prepareGeneAssociations,
			strip: stripGeneAssociation,
			isSame: haveSameGeneAssociations,
		});

	/**
	 * Saves the rows as `useSubResourceList` does. Rows that have been read are first checked for a gene typed
	 * rather than picked; while any has one, the save is refused without calling the API and those rows are
	 * marked.
	 *
	 * @param {number} [targetAlleleId]
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveGeneAssociations = useCallback(
		async (targetAlleleId) => {
			const marked = isLoading || loadError ? null : markRowsMissingGene(rows, errorMessages);
			if (marked) {
				setErrorMessages(marked);
				return { isSuccess: false, message: 'Select each gene from the autosuggest' };
			}
			return save(targetAlleleId);
		},
		[rows, errorMessages, setErrorMessages, isLoading, loadError, save]
	);

	return {
		alleleGeneAssociations: rows,
		setAlleleGeneAssociations: setRows,
		save: saveGeneAssociations,
		errorMessages,
		setErrorMessages,
		isLoading,
		loadError,
		...subResource,
	};
};
