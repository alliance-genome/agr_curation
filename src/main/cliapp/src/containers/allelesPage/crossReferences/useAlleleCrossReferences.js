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
	const [crossReferences, setCrossReferences] = useState([]);
	const [errorMessages, setErrorMessages] = useState({});
	const [isLoading, setIsLoading] = useState(false);
	const [isSaving, setIsSaving] = useState(false);
	const crossReferenceService = useMemo(() => new CrossReferenceService(), []);

	useEffect(() => {
		if (!alleleId) return undefined;

		let cancelled = false;
		const load = async () => {
			setIsLoading(true);
			try {
				const response = await crossReferenceService.getCrossReferencesForAllele(alleleId);
				const loaded = await keyAndSeed(response?.data?.entities ?? []);
				if (!cancelled) setCrossReferences(loaded);
			} catch (error) {
				console.warn(`Could not load cross references for allele ${alleleId}`, error);
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
	 * Replaces the allele's stored cross references with the rows held here.
	 *
	 * @param {number} [targetAlleleId] the allele to write to, for create, which has none on load
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const save = useCallback(
		async (targetAlleleId = alleleId) => {
			setIsSaving(true);
			setErrorMessages({});
			try {
				const response = await crossReferenceService.replaceCrossReferencesForAllele(
					targetAlleleId,
					crossReferences.map(stripUiFields)
				);
				setCrossReferences(await keyAndSeed(response?.data?.entities ?? []));
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
		[alleleId, crossReferenceService, crossReferences]
	);

	return { crossReferences, setCrossReferences, errorMessages, isLoading, isSaving, save };
};
