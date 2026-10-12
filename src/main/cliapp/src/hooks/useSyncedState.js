import { useEffect, useState } from 'react';

/**
 * Local state that follows an outside value. Editors use it to hold what the
 * control shows: the user's edits update it immediately, and a new value from the
 * caller (another row loaded, the parent normalising what was stored) replaces it.
 *
 * The reset runs in an effect whenever `externalValue` changes by identity, so the
 * control shows the old state for one render first. Pass a primitive or a
 * referentially stable value; an object or array rebuilt on every render resets the
 * state on every render and discards the user's edits.
 *
 * @template T
 * @param {T} externalValue - the value to start from and to reset to when it changes
 * @returns {[T, (value: T) => void]} the current value and its setter, as from `useState`
 */
export function useSyncedState(externalValue) {
	const [value, setValue] = useState(externalValue);

	useEffect(() => {
		setValue(externalValue);
	}, [externalValue]);

	return [value, setValue];
}
