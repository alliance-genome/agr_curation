import { useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import { AlleleSubResourceService } from '../../service/AlleleSubResourceService';
import { getIdentifier } from '../../utils/utils';
import { ALLELE_SUB_RESOURCES } from './constants';
import { buildDuplicateAllele, capitalize } from './utils';

/**
 * Fills the allele create form from the allele named by the `from` search parameter, which the Duplicate
 * buttons set. The form takes the source allele once per page load; after that it holds the curator's
 * edits, and a clear or a save and add another starts from a blank form.
 *
 * @param {Object} params
 * @param {Function} params.alleleDispatch - the create form's reducer dispatch
 * @param {Object} params.subResources - the create form's lists, keyed as in {@link ALLELE_SUB_RESOURCES}
 * @param {(fieldName: string) => boolean} params.isVisible - whether the form shows a field's section
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} `isLoading` is true while the source allele or any of its lists load
 */
export const useDuplicateAlleleSource = ({ alleleDispatch, subResources, isVisible, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const alleleService = useMemo(() => new AlleleService(), []);
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);

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
	const [listsLoading, setListsLoading] = useState(0);
	// The form's setters and section visibility as of the latest render, read when the source arrives.
	const formState = useRef({ subResources, isVisible });
	useEffect(() => {
		formState.current = { subResources, isVisible };
	});

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

		Object.entries(ALLELE_SUB_RESOURCES).forEach(async ([type, { path, label, buildCopies, copyOnlyWhenVisible }]) => {
			if (!buildCopies) return;
			if (copyOnlyWhenVisible && !formState.current.isVisible(copyOnlyWhenVisible)) return;

			setListsLoading((count) => count + 1);
			try {
				const response = await subResourceService.getList(sourceAllele.id, path);
				const copies = await buildCopies(response?.data?.entities);
				if (copies.length > 0) formState.current.subResources[type].setRows(copies);
			} catch (error) {
				console.warn(`Could not load ${label} for allele ${sourceAllele.id}`, error);
				toastError.current?.show({
					life: 7000,
					severity: 'warn',
					summary: `${capitalize(label)} not copied: `,
					detail: `Could not load the ${label} of allele ${getIdentifier(sourceAllele)}`,
					sticky: false,
				});
			} finally {
				setListsLoading((count) => count - 1);
			}
		});
	}, [sourceIdentifier, sourceAlleleData, sourceAlleleLoadFailed, alleleDispatch, subResourceService, toastError]);

	return { isLoading: sourceAlleleIsLoading || listsLoading > 0 };
};
