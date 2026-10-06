import { useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import { CrossReferenceService } from '../../service/CrossReferenceService';
import { getIdentifier } from '../../utils/utils';
import { buildDuplicateAllele } from './utils';
import { buildDuplicateCrossReferences, seedResourceDescriptors } from './crossReferences/utils';

/**
 * Fills the allele create form from the allele named by the `from` search parameter, which the Duplicate
 * buttons set. The form takes the source allele once per page load; after that it holds the curator's
 * edits, and a clear or a save and add another starts from a blank form.
 *
 * @param {Object} params
 * @param {Function} params.alleleDispatch - the create form's reducer dispatch
 * @param {Function} params.setCrossReferences - replaces the create form's cross reference rows
 * @param {boolean} params.crossReferencesVisible - whether the form shows its Cross References section
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} `isLoading` is true while the source allele or its cross references load
 */
export const useDuplicateAlleleSource = ({
	alleleDispatch,
	setCrossReferences,
	crossReferencesVisible,
	toastError,
}) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const alleleService = useMemo(() => new AlleleService(), []);
	const crossReferenceService = useMemo(() => new CrossReferenceService(), []);

	const {
		isFetching: sourceAlleleIsLoading,
		data: sourceAlleleData,
		isError: sourceAlleleLoadFailed,
	} = useQuery({
		queryKey: ['alleleDuplicateSource', sourceIdentifier],
		queryFn: () => alleleService.getAllele(sourceIdentifier),
		enabled: Boolean(sourceIdentifier),
		staleTime: Infinity,
		retry: false,
		refetchOnWindowFocus: false,
		refetchOnReconnect: false,
	});

	const sourceAlleleApplied = useRef(false);
	const [sourceCrossReferencesLoading, setSourceCrossReferencesLoading] = useState(false);

	useEffect(() => {
		if (!sourceIdentifier || sourceAlleleApplied.current) return;
		if (!sourceAlleleData && !sourceAlleleLoadFailed) return;

		sourceAlleleApplied.current = true;
		const sourceAllele = sourceAlleleData?.data?.entity;
		if (!sourceAllele) {
			toastError.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Page error: ',
				detail: `Could not load allele ${sourceIdentifier} to duplicate`,
				sticky: false,
			});
			return;
		}

		alleleDispatch({ type: 'SET', value: buildDuplicateAllele(sourceAllele) });

		// The copied rows keep the source's pages but not its curies, which the curator fills in for the new
		// allele. A hidden section could not show them, so it gets none.
		if (!crossReferencesVisible) return;

		const copySourceCrossReferences = async () => {
			setSourceCrossReferencesLoading(true);
			try {
				const response = await crossReferenceService.getCrossReferencesForAllele(sourceAllele.id);
				const copies = await seedResourceDescriptors(buildDuplicateCrossReferences(response?.data?.entities));
				if (copies.length > 0) setCrossReferences(copies);
			} catch (error) {
				console.warn(`Could not load cross references for allele ${sourceAllele.id}`, error);
				toastError.current?.show({
					life: 7000,
					severity: 'warn',
					summary: 'Cross references not copied: ',
					detail: `Could not load the cross references of allele ${getIdentifier(sourceAllele)}`,
					sticky: false,
				});
			} finally {
				setSourceCrossReferencesLoading(false);
			}
		};
		copySourceCrossReferences();
	}, [
		sourceIdentifier,
		sourceAlleleData,
		sourceAlleleLoadFailed,
		alleleDispatch,
		crossReferencesVisible,
		crossReferenceService,
		setCrossReferences,
		toastError,
	]);

	return { isLoading: sourceAlleleIsLoading || sourceCrossReferencesLoading };
};
