import { useEffect, useMemo, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { CassetteService } from '../../service/CassetteService';
import { buildDuplicateCassette, copyComponentRows } from './detail/utils';

/**
 * Fills the cassette create form from the cassette named by the `from` search parameter, which the Clone button
 * sets (SCRUM-6748): the cassette's own fields, and copies of the rows of each of its component lists, which are
 * read through their own endpoints. The form takes the source once per page load; after that it holds the
 * curator's edits, and a clear starts from a blank form.
 *
 * @param {Object} params
 * @param {Function} params.cassetteDispatch - the create form's reducer dispatch
 * @param {Array<{config: Object, components: Object}>} params.componentSections - the form's component lists
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the source cassette is loading
 */
export const useDuplicateCassetteSource = ({ cassetteDispatch, componentSections, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const cassetteService = useMemo(() => new CassetteService(), []);

	const {
		isFetching: sourceIsLoading,
		data: source,
		isError: sourceLoadFailed,
	} = useQuery({
		queryKey: ['cassetteDuplicateSource', sourceIdentifier],
		queryFn: async () => {
			const cassette = (await cassetteService.getCassette(sourceIdentifier))?.data?.entity;
			if (!cassette) return null;
			const lists = await Promise.all(
				componentSections.map(({ config }) => cassetteService.getComponentList(cassette.id, config.list))
			);
			return { cassette, lists: lists.map((response) => response?.data?.entities ?? []) };
		},
		enabled: Boolean(sourceIdentifier),
		staleTime: Infinity,
		retry: false,
		refetchOnWindowFocus: false,
		refetchOnReconnect: false,
	});

	const sourceApplied = useRef(false);

	useEffect(() => {
		if (!sourceIdentifier || sourceApplied.current) return;
		if (source === undefined && !sourceLoadFailed) return;

		sourceApplied.current = true;
		if (!source) {
			toastError.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Page error: ',
				detail: `Could not load cassette ${sourceIdentifier} to clone`,
				sticky: false,
			});
			return;
		}

		cassetteDispatch({ type: 'SET', value: buildDuplicateCassette(source.cassette) });
		componentSections.forEach(({ components }, index) => components.startOver(copyComponentRows(source.lists[index])));
	}, [sourceIdentifier, source, sourceLoadFailed, cassetteDispatch, componentSections, toastError]);

	return { isLoading: sourceIsLoading };
};
