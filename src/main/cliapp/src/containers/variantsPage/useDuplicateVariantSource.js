import { useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { VariantService } from '../../service/VariantService';
import { buildDuplicateVariant } from './utils';

/**
 * Fills the variant create form from the variant named by the `from` search parameter, which the
 * Clone buttons set. The form takes the source variant once per page load; after that it holds the
 * curator's edits.
 *
 * @param {Object} params
 * @param {Function} params.variantDispatch - the create form's reducer dispatch
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the source variant is loading
 */
export const useDuplicateVariantSource = ({ variantDispatch, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const variantService = useMemo(() => new VariantService(), []);

	const {
		isFetching: sourceVariantIsLoading,
		data: sourceVariantData,
		isError: sourceVariantLoadFailed,
	} = useQuery({
		queryKey: ['variantDuplicateSource', sourceIdentifier],
		queryFn: () => variantService.getVariant(sourceIdentifier),
		enabled: Boolean(sourceIdentifier),
		staleTime: Infinity,
		retry: false,
		refetchOnWindowFocus: false,
		refetchOnReconnect: false,
	});

	const sourceVariantApplied = useRef(false);

	useEffect(() => {
		if (!sourceIdentifier || sourceVariantApplied.current) return;
		if (!sourceVariantData && !sourceVariantLoadFailed) return;

		sourceVariantApplied.current = true;
		const sourceVariant = sourceVariantData?.data?.entity;
		if (!sourceVariant) {
			toastError.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Page error: ',
				detail: `Could not load variant ${sourceIdentifier} to clone`,
				sticky: false,
			});
			return;
		}

		variantDispatch({ type: 'SET', value: buildDuplicateVariant(sourceVariant) });
	}, [sourceIdentifier, sourceVariantData, sourceVariantLoadFailed, variantDispatch, toastError]);

	return { isLoading: sourceVariantIsLoading };
};
