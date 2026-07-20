import { useEffect, useState } from 'react';
import { InputText } from 'primereact/inputtext';

export function FilterComponentInputText({ isInEditMode, filterConfig, currentFilters, onFilter }) {
	const fieldSet = filterConfig.fieldSets[0];

	const [filterValue, setFilterValue] = useState(
		currentFilters && currentFilters[fieldSet.filterName]
			? currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString
			: ''
	);

	useEffect(() => {
		if (currentFilters && currentFilters[fieldSet.filterName]) {
			const existingFilter = currentFilters[fieldSet.filterName];
			const queryString = existingFilter[fieldSet.fields[0]]?.queryString;
			setFilterValue(queryString || '');
			// If saved filter is missing any fields (e.g. fieldSet was expanded), re-apply with all fields
			if (queryString && fieldSet.fields.some((f) => !existingFilter[f])) {
				const newFilter = {};
				fieldSet.fields.forEach((key) => {
					newFilter[key] = { queryString, tokenOperator: 'AND' };
				});
				onFilter({ ...currentFilters, [fieldSet.filterName]: newFilter });
			}
		} else {
			setFilterValue('');
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentFilters, fieldSet]);

	return (
		<InputText
			disabled={isInEditMode}
			value={filterValue}
			onChange={(e) => {
				setFilterValue(e.target.value);
				let filter = {};
				if (e.target.value.length !== 0) {
					fieldSet.fields.forEach((key) => {
						filter[key] = {
							queryString: e.target.value,
							tokenOperator: 'AND',
							// add filterConfig.useKeywords
						};
					});
					if (filterConfig.nonNullFields) {
						filter['nonNullFields'] = filterConfig.nonNullFields.fields;
					}
					if (filterConfig.nullFields) {
						filter['nullFields'] = filterConfig.nullFields.fields;
					}
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
