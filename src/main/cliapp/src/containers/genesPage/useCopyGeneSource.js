import { useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { GeneService } from '../../service/GeneService';
import { GeneSubResourceService } from '../../service/GeneSubResourceService';
import { capitalize, getIdentifier } from '../../utils/utils';
import { GENE_SUB_RESOURCES } from './constants';
import { buildGeneCopy } from './utils';

/**
 * Fills the gene create form with a copy of the gene named by the `from` search parameter, which the Clone buttons
 * and the table's copy action set. Loads that gene from the API and sets the form from it, then loads from the API
 * each of its lists that is copied, through the list's sub-resource, and sets copies of the rows on the form; a list
 * copied only while its section is shown is left empty when that section is hidden. The form is filled once per page
 * load, so a clear afterwards starts blank. A gene or list that cannot be loaded is reported in a toast.
 *
 * @param {Object} params
 * @param {Function} params.geneDispatch - the create form's reducer dispatch
 * @param {Object} params.subResources - the create form's lists, keyed as in {@link GENE_SUB_RESOURCES}
 * @param {(fieldName: string) => boolean} params.isVisible - whether the form shows a field's section
 * @param {Object} params.toastError - ref to the toast that reports a gene or list that cannot be loaded
 * @returns {{ isLoading: boolean }} true while the gene or any of its lists is loading, or the form is being filled
 */
export const useCopyGeneSource = ({ geneDispatch, subResources, isVisible, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const geneService = useMemo(() => new GeneService(), []);
	const subResourceService = useMemo(() => new GeneSubResourceService(), []);

	const {
		isFetching: sourceGeneIsLoading,
		data: sourceGeneData,
		isError: sourceGeneLoadFailed,
	} = useQuery({
		queryKey: ['geneCopySource', sourceIdentifier],
		queryFn: () => geneService.getGene(sourceIdentifier),
		enabled: Boolean(sourceIdentifier),
		staleTime: Infinity,
		retry: false,
		refetchOnWindowFocus: false,
		refetchOnReconnect: false,
	});

	const sourceGeneApplied = useRef(false);
	const [pendingLoads, setPendingLoads] = useState(0);
	// The form's lists and section visibility as of the latest render, read when the source arrives.
	const formState = useRef({ subResources, isVisible });
	useEffect(() => {
		formState.current = { subResources, isVisible };
	});

	useEffect(() => {
		if (!sourceIdentifier || sourceGeneApplied.current) return;
		if (!sourceGeneData && !sourceGeneLoadFailed) return;

		sourceGeneApplied.current = true;
		const sourceGene = sourceGeneData?.data?.entity;
		if (!sourceGene) {
			toastError.current?.show({
				life: 7000,
				severity: 'error',
				summary: 'Page error: ',
				detail: `Could not load gene ${sourceIdentifier} to clone`,
				sticky: false,
			});
			return;
		}

		const trackLoad = async (load) => {
			setPendingLoads((count) => count + 1);
			try {
				await load();
			} finally {
				setPendingLoads((count) => count - 1);
			}
		};

		trackLoad(async () => {
			try {
				geneDispatch({ type: 'SET', value: await buildGeneCopy(sourceGene, formState.current.isVisible) });
			} catch (error) {
				console.warn(`Could not copy gene ${sourceGene.id}`, error);
				toastError.current?.show({
					life: 7000,
					severity: 'error',
					summary: 'Page error: ',
					detail: `Could not load gene ${sourceIdentifier} to clone`,
					sticky: false,
				});
			}
		});

		Object.entries(GENE_SUB_RESOURCES).forEach(([type, { path, label, buildCopies, copyOnlyWhenVisible }]) => {
			if (!buildCopies) return;
			if (copyOnlyWhenVisible && !formState.current.isVisible(copyOnlyWhenVisible)) return;

			trackLoad(async () => {
				try {
					const response = await subResourceService.getList(sourceGene.id, path);
					const copies = await buildCopies(response?.data?.entities);
					if (copies.length > 0) formState.current.subResources[type].setRows(copies);
				} catch (error) {
					console.warn(`Could not load ${label} for gene ${sourceGene.id}`, error);
					toastError.current?.show({
						life: 7000,
						severity: 'warn',
						summary: `${capitalize(label)} not copied: `,
						detail: `Could not load the ${label} of gene ${getIdentifier(sourceGene)}`,
						sticky: false,
					});
				}
			});
		});
	}, [sourceIdentifier, sourceGeneData, sourceGeneLoadFailed, geneDispatch, subResourceService, toastError]);

	return { isLoading: sourceGeneIsLoading || pendingLoads > 0 };
};
