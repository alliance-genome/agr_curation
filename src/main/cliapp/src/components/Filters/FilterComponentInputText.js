import React, { useEffect, useState } from "react";
import { InputText } from 'primereact/inputtext';

export function FilterComponentInputText({ isEnabled, filterConfig, currentFilters, onFilter }) {
	
	const fieldSet = filterConfig.fieldSets[0];

	const [filterValue, setFilterValue] = useState(currentFilters && currentFilters[fieldSet.filterName] ? currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString : '');

	useEffect(() => {
		setFilterValue(currentFilters && currentFilters[fieldSet.filterName] ? currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString : '');
	}, [filterValue, currentFilters, fieldSet]);

	return (
		<InputText
			disabled={!isEnabled}
			value={filterValue}
			onChange={(e) => {
				setFilterValue(e.target.value);
				let filter = {};
				if (e.target.value.length !== 0) {
					fieldSet.fields.forEach((key) => {
						filter[key] = {
							queryString : e.target.value,
							tokenOperator : "AND",
							// add filterConfig.useKeywords
						}
					});
					if(filterConfig.nonNullFields) {
						filter['nonNullFields'] = filterConfig.nonNullFields.fields;
					}
					if(filterConfig.nullFields) {
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
	)
}

