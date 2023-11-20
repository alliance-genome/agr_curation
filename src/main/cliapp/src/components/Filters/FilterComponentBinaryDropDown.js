import React, { useRef } from "react";
import { Dropdown } from "primereact/dropdown";

export function FilterComponentBinaryDropDown({ isInEditMode, filterConfig, currentFilters, onFilter }) {
	const options = useRef(["true", "false"]);

	const fieldSet = filterConfig.fieldSets[0];

	return (
		<Dropdown
			disabled={isInEditMode}
			value={currentFilters?.[fieldSet?.filterName]?.[fieldSet?.fields?.[0]]?.queryString}
			options={options.current}
			showClear
			placeholder="Select"
			onChange={(e) => {
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

