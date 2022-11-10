import React, {useEffect, useState} from "react";
import { Dropdown } from "primereact/dropdown";

export function FilterComponentDropDown({ isEnabled, field, tokenOperator= "OR" ,filterName, currentFilters, onFilter, options, optionField }) {
		const [filterValue, setFilterValue] = useState(()=> {
				for(let i=0; i<options.length; i++){
						if(currentFilters && currentFilters[filterName] && options[i][optionField] === currentFilters[filterName][field].queryString){
								return options[i];
						}
				}
				return null;
		});

	 useEffect(() => {
			 if(currentFilters && currentFilters[filterName]){
					 for (let i = 0; i < options.length; i++) {
							 if (currentFilters[filterName] && options[i][optionField] === currentFilters[filterName][field].queryString) {
									 setFilterValue(options[i]);
							 }
					 }
			 }else {
					 setFilterValue(null);
			 }
		}, [options, optionField, filterValue, currentFilters, field, filterName]);

		return (
				<Dropdown
						disabled={!isEnabled}
						value={filterValue}
						options={options}
						showClear
						optionLabel={optionField}
						placeholder="Select"
						style={{ width: '100%', display: 'inline-flex' }}
						onChange={(e) => {
								console.log(e.target.value);
								setFilterValue(e.target.value);
								let filter = {};
								if(e.target.value && e.target.value[optionField].length !== 0) {
										filter[field] = {
												queryString : e.target.value[optionField],
												tokenOperator : tokenOperator
										};
								} else {
										filter = null;
								}
								const filtersCopy = currentFilters;
								if (filter === null) {
										delete filtersCopy[filterName];
								} else {
										filtersCopy[filterName] = filter;
								}
								onFilter(filtersCopy);
						}}
				/>
		)
}

