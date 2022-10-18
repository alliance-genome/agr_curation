import React, { useEffect, useState } from "react";
import { InputText } from 'primereact/inputtext';

export function FilterComponentInputText({ isEnabled, fields, nonNullFields, nullFields, filterName, currentFilters, onFilter, tokenOperator="AND" }) {
		const [filterValue, setFilterValue] =
			useState(currentFilters && currentFilters[filterName] ? currentFilters[filterName][fields[0]].queryString : '');

		useEffect(() => {
				setFilterValue(currentFilters && currentFilters[filterName] ? currentFilters[filterName][fields[0]].queryString : '')
		}, [filterValue, currentFilters, fields, filterName]);

		return (
				<InputText
						disabled={!isEnabled}
						value={filterValue}
						onChange={(e) => {
								setFilterValue(e.target.value);
								let filter = {};
								if (e.target.value.length !== 0) {
										fields.forEach((key) => {
												filter[key] = {
														queryString : e.target.value,
														tokenOperator : tokenOperator
												}
										});
										if(nonNullFields){
											filter['nonNullFields'] = nonNullFields;
										}
										if(nullFields){
											filter['nullFields'] = nullFields;
										}
								} else {
										filter = null;
								}

								const filtersCopy = currentFilters ? currentFilters : {};
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

