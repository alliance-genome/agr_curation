import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';

export function useMultiSelectAggregationQuery({
	filterConfig,
	currentFilters,
	endpoint,
	setSelectableOptions,
	setSelectedOptions,
	searchService,
	fieldSet,
}) {
	const { data: aggData, isSuccess } = useQuery({
		queryKey: [filterConfig?.aggregationFieldSet],
		queryFn: () => searchService.search(endpoint, 0, 0, null, {}, {}, filterConfig?.aggregationFieldSet.fields),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
		staleTime: Infinity,
	});

	// The filter's own field may target the _keyword variant directly (for exact-match multiselects),
	// but the aggregation endpoint always responds keyed by the plain field name.
	const aggregationKey = fieldSet.fields[0]?.replace(/_keyword$/, '');

	useEffect(() => {
		let tmp = [];
		if (isSuccess && aggData?.aggregations) {
			for (let key in aggData.aggregations[aggregationKey]) {
				tmp.push({
					optionLabel: key,
					optionValue: aggData.aggregations[aggregationKey][key],
				});
			}
			tmp.sort((a, b) => (a.optionLabel > b.optionLabel ? 1 : -1));
			setSelectableOptions(tmp);
		}

		if (currentFilters && currentFilters[fieldSet.filterName]) {
			let newSelectedOptions = [];
			let rawQueryString = currentFilters[fieldSet.filterName][fieldSet.fields[0]]?.queryString || '';
			let queryStrings = rawQueryString.match(/"([^"]*)"/g)?.map((s) => s.slice(1, -1)) || rawQueryString.split(' ');
			for (let i in tmp) {
				for (let j in queryStrings) {
					if (tmp[i].optionLabel === queryStrings[j].toLowerCase()) {
						newSelectedOptions.push(tmp[i]);
					}
				}
			}
			setSelectedOptions(newSelectedOptions);
		} else {
			setSelectedOptions([]);
		}
	}, [
		aggData,
		isSuccess,
		currentFilters,
		fieldSet?.fields,
		fieldSet?.filterName,
		setSelectedOptions,
		setSelectableOptions,
	]);
}
