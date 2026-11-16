import { useCallback } from 'react';
import { useSubResourceList } from './useSubResourceList';

/**
 * One of an entity's lists, read and written through its own sub-resource rather than with the entity.
 *
 * @param {Object} entry the list's registry entry: `path`, `label`, `errorField`, `prepare`, `strip` and `isSame`, as
 *   `useSubResourceList` takes them, and optionally `markUnsavableRows` with `unsavableRowsMessage`
 * @param {import('../service/SubResourceService').SubResourceService} service the entity's sub-resource service, the
 *   same instance on every render
 * @param {number} [ownerId] the entity to read on load. Create passes none and saves once it has one.
 * @returns {Object} what `useSubResourceList` returns, with the list's `label` and a `save` that first refuses rows the
 *   entry's `markUnsavableRows` marks
 */
export const useEntitySubResource = (entry, service, ownerId) => {
	const { path, label, errorField, prepare, strip, isSame, markUnsavableRows, unsavableRowsMessage } = entry;
	const load = useCallback((targetOwnerId) => service.getList(targetOwnerId, path), [service, path]);
	const replace = useCallback((targetOwnerId, rows) => service.replaceList(targetOwnerId, path, rows), [service, path]);

	const subResource = useSubResourceList({
		ownerId,
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
	 * Saves the rows as `useSubResourceList` does. Rows that have been read are first checked with the entry's
	 * `markUnsavableRows`; while it marks any, the save is refused without calling the API.
	 *
	 * @param {number} [targetOwnerId]
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveRows = useCallback(
		async (targetOwnerId) => {
			const marked = markUnsavableRows && !isLoading && !loadError ? markUnsavableRows(rows, errorMessages) : null;
			if (marked) {
				setErrorMessages(marked);
				return { isSuccess: false, message: unsavableRowsMessage };
			}
			return save(targetOwnerId);
		},
		[markUnsavableRows, unsavableRowsMessage, rows, errorMessages, setErrorMessages, isLoading, loadError, save]
	);

	return { ...subResource, label, save: saveRows };
};
