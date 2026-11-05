import { useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { AlleleService } from '../../service/AlleleService';
import { AlleleSubResourceService } from '../../service/AlleleSubResourceService';
import { getIdentifier } from '../../utils/utils';
import { ALLELE_SUB_RESOURCES } from './constants';
import { buildAlleleCopy } from './utils';
import { capitalize } from '../../utils/utils';

/**
 * Fills the allele create form with a copy of the allele named by the `from` search parameter, which the Clone
 * buttons and the table's copy action set. Loads that allele from the API and sets the form from it, then loads
 * from the API each of its lists that is copied, through the list's sub-resource, and sets copies of the rows on
 * the form; a list copied only while its section is shown is left empty when that section is hidden. The form is
 * filled once per page load, so a clear afterwards starts blank. An allele or list that cannot be loaded is
 * reported in a toast.
 *
 * @param {Object} params
 * @param {Function} params.alleleDispatch - the create form's reducer dispatch
 * @param {Object} params.subResources - the create form's lists, keyed as in {@link ALLELE_SUB_RESOURCES}
 * @param {(fieldName: string) => boolean} params.isVisible - whether the form shows a field's section
 * @param {Object} params.toastError - ref to the toast that reports a source that cannot be loaded
 * @returns {{ isLoading: boolean }} `isLoading` is true while the source allele or any of its lists load
 */
export const useCopyAlleleSource = ({ alleleDispatch, subResources, isVisible, toastError }) => {
	const [searchParams] = useSearchParams();
	const sourceIdentifier = searchParams.get('from');
	const alleleService = useMemo(() => new AlleleService(), []);
	const subResourceService = useMemo(() => new AlleleSubResourceService(), []);

	const {
		isFetching: sourceAlleleIsLoading,
		data: sourceAlleleData,
		isError: sourceAlleleLoadFailed,
	} = useQuery({
		queryKey: ['alleleCopySource', sourceIdentifier],
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
				detail: `Could not load allele ${sourceIdentifier} to clone`,
				sticky: false,
			});
			return;
		}

		alleleDispatch({ type: 'SET', value: buildAlleleCopy(sourceAllele) });

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
