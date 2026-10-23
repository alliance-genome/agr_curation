import { useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { AffectedGenomicModelService } from '../../service/AffectedGenomicModelService';
import { buildDuplicateAgm } from './utils';

/**
 * Fills the AGM create form from the AGM named by the `from` search parameter, which the
 * Duplicate buttons set. The form takes the source AGM once per page load; after that it holds
 * the curator's edits, and a clear starts from a blank form.
 *
 * @param {Object} params
 * @param {Function} params.agmDispatch - the create form's reducer dispatch
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the source AGM is loading
 */
export const useDuplicateAgmSource = ({ agmDispatch, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const agmService = useMemo(() => new AffectedGenomicModelService(), []);

	const {
		isFetching: sourceAgmIsLoading,
		data: sourceAgmData,
		isError: sourceAgmLoadFailed,
	} = useQuery({
		queryKey: ['agmDuplicateSource', sourceIdentifier],
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
				detail: `Could not load AGM ${sourceIdentifier} to duplicate`,
				sticky: false,
			});
			return;
		}

		agmDispatch({ type: 'SET', value: buildDuplicateAgm(sourceAgm) });
	}, [sourceIdentifier, sourceAgmData, sourceAgmLoadFailed, agmDispatch, toastError]);

	return { isLoading: sourceAgmIsLoading };
};
