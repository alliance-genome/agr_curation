import { useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { VariantService } from '../../service/VariantService';
import { buildVariantCopy } from './utils';

/**
 * Fills the variant create form with a copy of the variant named by the `from` search parameter, which the
 * Clone buttons and the table's copy action set. Loads that variant, with its lists, from the API in one request
 * and sets the form from it. The form is filled once per page load, so a clear afterwards starts blank. A variant
 * that cannot be loaded is reported in a toast.
 *
 * @param {Object} params
 * @param {Function} params.variantDispatch - the create form's reducer dispatch
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the source variant is loading
 */
export const useCopyVariantSource = ({ variantDispatch, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const variantService = useMemo(() => new VariantService(), []);

	const {
		isFetching: sourceVariantIsLoading,
		data: sourceVariantData,
		isError: sourceVariantLoadFailed,
	} = useQuery({
		queryKey: ['variantCopySource', sourceIdentifier],
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

		variantDispatch({ type: 'SET', value: buildVariantCopy(sourceVariant) });
	}, [sourceIdentifier, sourceVariantData, sourceVariantLoadFailed, variantDispatch, toastError]);

	return { isLoading: sourceVariantIsLoading };
};
