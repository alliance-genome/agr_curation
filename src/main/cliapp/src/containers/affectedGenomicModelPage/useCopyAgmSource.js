import { useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import { buildAgmCopy } from './utils';

/**
 * Fills the AGM create form with a copy of the AGM named by the `from` search parameter, which the Clone
 * buttons and the table's copy action set. Loads that AGM, with its lists, from the API in one request and sets
 * the form from it. The form is filled once per page load, so a clear afterwards starts blank. An AGM that cannot
 * be loaded is reported in a toast.
 *
 * @param {Object} params
 * @param {Function} params.agmDispatch - the create form's reducer dispatch
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the source AGM is loading
 */
export const useCopyAgmSource = ({ agmDispatch, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const agmService = useMemo(() => new AffectedGenomicModelService(), []);

	const {
		isFetching: sourceAgmIsLoading,
		data: sourceAgmData,
		isError: sourceAgmLoadFailed,
	} = useQuery({
		queryKey: ['agmCopySource', sourceIdentifier],
		queryFn: () => agmService.getAGM(sourceIdentifier),
		enabled: Boolean(sourceIdentifier),
		staleTime: Infinity,
		retry: false,
		refetchOnWindowFocus: false,
		refetchOnReconnect: false,
	});

	const sourceAgmApplied = useRef(false);

	useEffect(() => {
		if (!sourceIdentifier || sourceAgmApplied.current) return;
		if (!sourceAgmData && !sourceAgmLoadFailed) return;

		sourceAgmApplied.current = true;
		const sourceAgm = sourceAgmData?.data?.entity;
		if (!sourceAgm) {
			toastError.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Page error: ',
				detail: `Could not load AGM ${sourceIdentifier} to clone`,
				sticky: false,
			});
			return;
		}

		agmDispatch({ type: 'SET', value: buildAgmCopy(sourceAgm) });
	}, [sourceIdentifier, sourceAgmData, sourceAgmLoadFailed, agmDispatch, toastError]);

	return { isLoading: sourceAgmIsLoading };
};
