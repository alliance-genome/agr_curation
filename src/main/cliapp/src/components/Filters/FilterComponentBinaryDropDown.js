import React, {useEffect, useState, useRef} from "react";
import { Dropdown } from "primereact/dropdown";

export function FilterComponentBinaryDropDown({ isEnabled, filterConfig, currentFilters, onFilter }) {
	const options = useRef(["true", "false"]);

	const fieldSet = filterConfig.fieldSets[0];

	const [filterValue, setFilterValue] = useState(() => {
		
		for(let i = 0; i < options.current.length; i++) {
			if(currentFilters && currentFilters[fieldSet.filterName] && options.current[i] === currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString){
				return options.current[i];
			}
		}
		return null;
	});

	useEffect(() => {
		if(currentFilters && currentFilters[fieldSet.filterName]) {
			for (let i = 0; i < options.current.length; i++) {
				if (currentFilters[fieldSet.filterName] && options[i] === currentFilters[fieldSet.filterName][fieldSet.fields[0]].queryString) {
					setFilterValue(options.current[i]);
				}
			}
		} else {
			setFilterValue(null);
		}
	}, [filterValue, currentFilters, fieldSet, options]);

	return (
		<Dropdown
			disabled={!isEnabled}
			value={filterValue}
			options={options.current}
			showClear
			placeholder="Select"
			style={{ width: '100%', display: 'inline-flex' }}
			onChange={(e) => {
				setFilterValue(e.target.value);
				let filter = {};
				if(e.target.value && e.target.value.length !== 0) {
					filter[fieldSet.fields[0]] = {
						queryString : e.target.value,
						tokenOperator : "OR"
					};
				} else {
					filter = null;
				}

				//undefined check needs to be in place. Otherwise, the else block below will throw an error 
				const filtersCopy = currentFilters  ? currentFilters : {};
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

