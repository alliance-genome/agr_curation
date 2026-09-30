import { useCallback, useEffect, useMemo, useState } from 'react';
import { CrossReferenceService } from '../../../service/CrossReferenceService';
import { addDataKey } from '../utils';
import { seedResourceDescriptors, stripUiFields } from './utils';

/**
 * Maps the API's index keyed cross reference errors onto the rows they belong to, so the table can
 * render each against its own row.
 */
const mapErrorsToRows = (data, crossReferences) => {
	const indexedErrors = data?.supplementalData?.errorMap?.crossReferences ?? {};
	const errorMessages = {};

	Object.keys(indexedErrors).forEach((index) => {
		const row = crossReferences[Number(index)];
		if (!row) return;

		errorMessages[row.dataKey] = {};
		Object.keys(indexedErrors[index]).forEach((field) => {
			errorMessages[row.dataKey][field] = { severity: 'error', message: indexedErrors[index][field] };
		});
	});

	return errorMessages;
};

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
	const [crossReferences, setStoredCrossReferences] = useState([]);
	const [errorMessages, setErrorMessages] = useState({});
	const [isLoading, setIsLoading] = useState(Boolean(alleleId));
	const [loadError, setLoadError] = useState(null);
	const [isSaving, setIsSaving] = useState(false);
	const [isDirty, setIsDirty] = useState(false);
	const crossReferenceService = useMemo(() => new CrossReferenceService(), []);

	// Every change made through here counts as an edit, so a page saving the allele can leave rows the
	// curator did not touch unwritten. Loading and saving set the rows directly and leave none pending.
	const setCrossReferences = useCallback((update) => {
		setIsDirty(true);
		setStoredCrossReferences(update);
	}, []);

	useEffect(() => {
		if (!alleleId) return undefined;

		let cancelled = false;
		const load = async () => {
			setIsLoading(true);
			setLoadError(null);
			try {
				const response = await crossReferenceService.getCrossReferencesForAllele(alleleId);
				const loaded = await keyAndSeed(response?.data?.entities ?? []);
				if (!cancelled) {
					setStoredCrossReferences(loaded);
					setIsDirty(false);
				}
			} catch (error) {
				console.warn(`Could not load cross references for allele ${alleleId}`, error);
				// Held rather than swallowed so saving can refuse. An empty table that failed to load is
				// indistinguishable from an allele with none, and saving it would delete them.
				if (!cancelled) setLoadError(error);
			} finally {
				if (!cancelled) setIsLoading(false);
			}
		};

		load();
		return () => {
			cancelled = true;
		};
	}, [alleleId, crossReferenceService]);

	/**
	 * Replaces the allele's stored cross references with the rows held here. Refuses, without calling the
	 * API, while the rows are still loading or after they failed to load.
	 *
	 * @param {number} [targetAlleleId] the allele to write to, for create, which has none on load
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const save = useCallback(
		async (targetAlleleId = alleleId) => {
			// A save replaces the stored list with the rows held here, so it has to wait for the read. Rows
			// still loading, or that failed to load, would submit an empty list and delete every cross
			// reference the allele has.
			if (isLoading || loadError) {
				return { isSuccess: false, message: 'These cross references have not been read, so they were not saved' };
			}

			setIsSaving(true);
			setErrorMessages({});
			try {
				const response = await crossReferenceService.replaceCrossReferencesForAllele(
					targetAlleleId,
					crossReferences.map(stripUiFields)
				);
				setStoredCrossReferences(await keyAndSeed(response?.data?.entities ?? []));
				setIsDirty(false);
				return { isSuccess: true };
			} catch (error) {
				const data = error?.response?.data;
				setErrorMessages(mapErrorsToRows(data, crossReferences));
				return {
					isSuccess: false,
					message: data?.errorMessage ?? `${error?.response?.status} ${error?.response?.statusText}`,
				};
			} finally {
				setIsSaving(false);
			}
		},
		[alleleId, crossReferenceService, crossReferences, isLoading, loadError]
	);

	return {
		crossReferences,
		setCrossReferences,
		errorMessages,
		isLoading,
		loadError,
		isSaving,
		isDirty,
		save,
	};
};
