import { useCallback, useEffect, useMemo, useState } from 'react';

/**
 * Maps the API's index keyed row errors for a field onto the rows they belong to, so a table can
 * render each against its own row.
 */
const mapErrorsToRows = (data, errorField, rows) => {
	const indexedErrors = data?.supplementalData?.errorMap?.[errorField] ?? {};
	const errorMessages = {};

	Object.keys(indexedErrors).forEach((index) => {
		const row = rows[Number(index)];
		if (!row) return;

		errorMessages[row.dataKey] = {};
		Object.keys(indexedErrors[index]).forEach((field) => {
			errorMessages[row.dataKey][field] = { severity: 'error', message: indexedErrors[index][field] };
		});
	});

	return errorMessages;
};

/**
 * A list an entity holds through a sub-resource of its own rather than in the entity's payload: read
 * when the entity is known, edited as rows, and written back as a whole.
 *
 * Every function option must keep its identity across renders.
 *
 * @param {Object} options
 * @param {number} [options.ownerId] the entity to read on load. Create passes none and saves once it has one.
 * @param {string} options.label what the rows are, for messages, such as `'cross references'`
 * @param {string} options.errorField the field the API keys the rows' errors under
 * @param {(ownerId: number) => Promise<Object>} options.load reads the stored rows
 * @param {(ownerId: number, rows: Object[]) => Promise<Object>} options.replace writes the rows, as `strip` leaves them
 * @param {(rows: Object[]) => (Object[]|Promise<Object[]>)} options.prepare readies rows the API returned for the table
 * @param {(row: Object) => Object} options.strip a row as the API takes it
 * @param {(rows: Object[], otherRows: Object[]) => boolean} options.isSame whether two lists hold the same rows
 */
export const useSubResourceList = ({ ownerId, label, errorField, load, replace, prepare, strip, isSame }) => {
	const [rows, setRows] = useState([]);
	// The rows as last loaded or saved from the API, which the rows on screen are compared with.
	const [savedRows, setSavedRows] = useState([]);
	const [errorMessages, setErrorMessages] = useState({});
	const [isLoading, setIsLoading] = useState(Boolean(ownerId));
	const [loadError, setLoadError] = useState(null);
	const [isSaving, setIsSaving] = useState(false);

	// Whether the rows differ from those last loaded or saved from the API, so a page saving the entity can skip
	// writing them when none have changed.
	const isDirty = useMemo(() => !isSame(rows, savedRows), [isSame, rows, savedRows]);

	const showSavedRows = useCallback((loadedRows) => {
		setRows(loadedRows);
		setSavedRows(loadedRows);
	}, []);

	/**
	 * Empties the rows and forgets those last saved, so the list starts over as one with nothing stored,
	 * as a create form does for its next entity.
	 */
	const reset = useCallback(() => {
		showSavedRows([]);
		setErrorMessages({});
	}, [showSavedRows]);

	useEffect(() => {
		if (!ownerId) return undefined;

		let cancelled = false;
		const read = async () => {
			setIsLoading(true);
			setLoadError(null);
			try {
				const response = await load(ownerId);
				const loadedRows = await prepare(response?.data?.entities ?? []);
				if (!cancelled) {
					showSavedRows(loadedRows);
				}
			} catch (error) {
				console.warn(`Could not load ${label} for ${ownerId}`, error);
				// Held rather than swallowed so saving can refuse. An empty table that failed to load is
				// indistinguishable from an entity with no rows, and saving it would delete them.
				if (!cancelled) setLoadError(error);
			} finally {
				if (!cancelled) setIsLoading(false);
			}
		};

		read();
		return () => {
			cancelled = true;
		};
	}, [ownerId, label, load, prepare, showSavedRows]);

	/**
	 * Replaces the entity's stored rows with the rows held here. Refuses, without calling the API, while
	 * the rows are still loading or after they failed to load.
	 *
	 * @param {number} [targetOwnerId] the entity to write to, for create, which has none on load
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const save = useCallback(
		async (targetOwnerId = ownerId) => {
			// A save replaces the stored list with the rows held here, so it has to wait for the read. Rows
			// still loading, or that failed to load, would submit an empty list and delete every row the
			// entity has.
			if (isLoading || loadError) {
				return { isSuccess: false, message: `These ${label} have not been read, so they were not saved` };
			}

			setIsSaving(true);
			setErrorMessages({});
			try {
				const response = await replace(targetOwnerId, rows.map(strip));
				showSavedRows(await prepare(response?.data?.entities ?? []));
				return { isSuccess: true };
			} catch (error) {
				const data = error?.response?.data;
				setErrorMessages(mapErrorsToRows(data, errorField, rows));
				return {
					isSuccess: false,
					message: data?.errorMessage ?? `${error?.response?.status} ${error?.response?.statusText}`,
				};
			} finally {
				setIsSaving(false);
			}
		},
		[ownerId, label, errorField, replace, prepare, strip, rows, isLoading, loadError, showSavedRows]
	);

	return {
		rows,
		setRows,
		errorMessages,
		isLoading,
		loadError,
		isSaving,
		isDirty,
		save,
		reset,
		setErrorMessages,
	};
};
