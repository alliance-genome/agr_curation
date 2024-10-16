import React, { useState } from 'react';
import { MultiSelect } from 'primereact/multiselect';
import { useQuery } from '@tanstack/react-query';
import { SearchService } from '../../service/SearchService';

export function FilterComponentMultiSelect({ isInEditMode, filterConfig, currentFilters, onFilter, endpoint }) {
	const [selectedOptions, setSelectedOptions] = useState([]);
	const [selectableOptions, setSelectableOptions] = useState([]);

	const fieldSet = filterConfig.fieldSets[0];

	const searchService = new SearchService();

	useQuery(
		[filterConfig.aggregationFieldSet, currentFilters],
		() => searchService.search(endpoint, 0, 0, null, {}, {}, filterConfig.aggregationFieldSet.fields),
		{
			onSuccess: (data) => {
				let tmp = [];
				if (data.aggregations) {
					for (let key in data.aggregations[fieldSet.fields[0]]) {
						tmp.push({
							optionLabel: key,
							optionValue: data.aggregations[fieldSet.fields[0]][key],
						});
					}
				}
				tmp.sort((a, b) => (a.optionLabel > b.optionLabel ? 1 : -1));
				setSelectableOptions(tmp);
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
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
		}
	);

	const panelFooterTemplate = () => {
		const length = selectedOptions ? selectedOptions.length : 0;
		return (
			<div style={{ padding: '0.9rem' }}>
				<b>{length}</b> item{length > 1 ? 's' : ''} selected.
			</div>
		);
	};

	const itemTemplate = (option) => {
		return <div>{option.optionLabel}</div>;
	};

	const upperCaseItemTemplate = (option) => {
		return <div>{option.optionLabel.toUpperCase()}</div>;
	};

	const templateSelector = (option) => {
		if (
			fieldSet.filterName === 'dataProviderFilter' ||
			fieldSet.filterName === 'secondaryDataProviderFilter' ||
			fieldSet.filterName === 'evidenceCodesFilter'
		) {
			return upperCaseItemTemplate(option);
		} else {
			return itemTemplate(option);
		}
	};

	return (
		<MultiSelect
			disabled={isInEditMode}
			value={selectedOptions}
			options={selectableOptions}
			placeholder="Select"
			display="chip"
			optionLabel="optionLabel"
			style={{ width: '100%', display: 'inline-flex' }}
			itemTemplate={templateSelector}
			filter
			className={'multiselect-custom'}
			panelFooterTemplate={panelFooterTemplate}
			onChange={(e) => {
				setSelectedOptions(e.target.value);
				let filter = {};
				let queryString = '';
				let delim = '';
				if (e.target.value && e.target.value.length !== 0) {
					for (let i in e.target.value) {
						queryString += delim + e.target.value[i].optionLabel;
						delim = ' ';
					}
					filter[fieldSet.fields[0]] = {
						useKeywordFields: fieldSet.useKeywordFields,
						tokenOperator: 'OR',
						queryString: queryString,
					};
				} else {
					filter = null;
				}
				const filtersCopy = currentFilters ? currentFilters : {};
				if (filter === null) {
					delete filtersCopy[fieldSet.filterName];
				} else {
					filtersCopy[fieldSet.filterName] = filter;
				}
				onFilter(filtersCopy);
			}}
		/>
	);
}
