import { capitalize } from './utils';

/**
 * An entity's sub-resource lists taken together, for a page that saves, resets and tracks them as one.
 *
 * @param {Object<string, Object>} subResources each list as `useEntitySubResource` returns it, keyed by the name the
 *   page provides it under, in the order they are saved
 * @returns {{saveDirty: Function, isDirty: boolean, isSaving: boolean, resetAll: Function,
 *   dirtySteps: Array<{key: string, label: string}>}} a save of the edited lists, whether any is edited or being
 *   written, a reset of them all, and the edited lists as save progress rows
 */
export const subResourceSet = (subResources) => {
	const lists = Object.entries(subResources);

	/**
	 * Writes each list that has been edited, one after another, each as its whole list. A list that has not been
	 * edited is not written, so its stored rows stay as they are, and the marks a past save or a note edit left
	 * on its rows are cleared.
	 *
	 * @param {number} [targetOwnerId] the entity to write to, for create, which has none on load
	 * @param {Object} [progress] told about each list as it is written, by its key
	 * @param {(key: string) => void} [progress.onStart]
	 * @param {(key: string, outcome: {isSuccess: boolean, message?: string}) => void} [progress.onSettle]
	 * @returns {Promise<Array<{label: string, outcome: {isSuccess: boolean, message?: string}}>>} the outcome of
	 *   each list written, in order
	 */
	const saveDirty = async (targetOwnerId, { onStart, onSettle } = {}) => {
		const outcomes = [];
		for (const [key, subResource] of lists) {
			if (subResource.isDirty) {
				onStart?.(key);
				const outcome = await subResource.save(targetOwnerId);
				onSettle?.(key, outcome);
				outcomes.push({ label: subResource.label, outcome });
			} else {
				subResource.setErrorMessages({});
			}
		}
		return outcomes;
	};

	const resetAll = () => lists.forEach(([, subResource]) => subResource.reset());

	return {
		isDirty: lists.some(([, subResource]) => subResource.isDirty),
		isSaving: lists.some(([, subResource]) => subResource.isSaving),
		dirtySteps: lists
			.filter(([, subResource]) => subResource.isDirty)
			.map(([key, subResource]) => ({ key, label: capitalize(subResource.label) })),
		saveDirty,
		resetAll,
	};
};
