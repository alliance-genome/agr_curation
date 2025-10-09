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
		queryKey: [filterConfig?.aggregationFieldSet, currentFilters],
		queryFn: () => searchService.search(endpoint, 0, 0, null, {}, {}, filterConfig?.aggregationFieldSet.fields),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	useEffect(() => {
		let tmp = [];
		if (isSuccess && aggData?.aggregations) {
			for (let key in aggData.aggregations[fieldSet.fields[0]]) {
				tmp.push({
					optionLabel: key,
					optionValue: aggData.aggregations[fieldSet.fields[0]][key],
				});
			}
			tmp.sort((a, b) => (a.optionLabel > b.optionLabel ? 1 : -1));
			setSelectableOptions(tmp);
		}

		if (currentFilters && currentFilters[fieldSet.filterName]) {
			let newSelectedOptions = [];
			let queryStrings = currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString.split(' ');
			for (let i in tmp) {
				for (let j in queryStrings) {
					if (tmp[i].optionLabel === queryStrings[j].toLowerCase()) {
						newSelectedOptions.push(tmp[i]);
					}
				}
			}
			if (newSelectedOptions.length > 0) setSelectedOptions(newSelectedOptions);
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
