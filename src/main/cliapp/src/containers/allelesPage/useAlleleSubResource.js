import { useCallback, useMemo } from 'react';
import { AlleleSubResourceService } from '../../service/AlleleSubResourceService';
import { useSubResourceList } from '../../hooks/useSubResourceList';
import { ALLELE_SUB_RESOURCES } from './constants';

/**
 * One of an allele's lists, read and written through its own sub-resource rather than with the allele.
 *
 * @param {keyof typeof ALLELE_SUB_RESOURCES} type which list
 * @param {number} [alleleId] the allele to read on load. Create passes none and saves once it has one.
 * @returns {Object} what `useSubResourceList` returns, with a `save` that first refuses rows the list's
 *   `markUnsavableRows` marks
 */
export const useAlleleSubResource = (type, alleleId) => {
	const { path, label, errorField, prepare, strip, isSame, markUnsavableRows, unsavableRowsMessage } =
		ALLELE_SUB_RESOURCES[type];
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);
	const load = useCallback(
		(targetAlleleId) => subResourceService.getList(targetAlleleId, path),
		[subResourceService, path]
	);
	const replace = useCallback(
		(targetAlleleId, rows) => subResourceService.replaceList(targetAlleleId, path, rows),
		[subResourceService, path]
	);

	const subResource = useSubResourceList({
		ownerId: alleleId,
		label,
		errorField,
		load,
		replace,
		prepare,
		strip,
		isSame,
	});
	const { rows, errorMessages, setErrorMessages, isLoading, loadError, save } = subResource;

	/**
	 * Saves the rows as `useSubResourceList` does. Rows that have been read are first checked with the list's
	 * `markUnsavableRows`; while it marks any, the save is refused without calling the API.
	 *
	 * @param {number} [targetAlleleId]
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveRows = useCallback(
		async (targetAlleleId) => {
			const marked = markUnsavableRows && !isLoading && !loadError ? markUnsavableRows(rows, errorMessages) : null;
			if (marked) {
				setErrorMessages(marked);
				return { isSuccess: false, message: unsavableRowsMessage };
			}
			return save(targetAlleleId);
		},
		[markUnsavableRows, unsavableRowsMessage, rows, errorMessages, setErrorMessages, isLoading, loadError, save]
	);

	return { ...subResource, save: saveRows };
};
