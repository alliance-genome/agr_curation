import React from 'react';
import { FilterComponentInputText } from './FilterComponentInputText';
import { FilterComponentBinaryDropDown } from './FilterComponentBinaryDropDown';
import { FilterComponentMultiSelect } from './FilterComponentMultiSelect';

export const FilterComponent = ({
	filterConfig,
	isEnabled,
	onFilter,
	aggregationFields,
	tableState,
	endpoint,
}) => {
	switch(filterConfig.filterComponentType) {
		case "input":
			return (
				<>
				<FilterComponentInputText
					filterConfig={filterConfig}
					isEnabled={isEnabled}
					currentFilters={tableState.filters}
					onFilter={onFilter}
				/>&nbsp;&nbsp;
				<i className="pi pi-filter" style={{ 'fontSize': '1em' }}></i>
				</>
			);

		case "dropdown":
			return (
				<>
				<FilterComponentBinaryDropDown
					filterConfig={filterConfig}
					isEnabled={isEnabled}
					currentFilters={tableState.filters}
					onFilter={onFilter}
				/>&nbsp;&nbsp;
				<i className="pi pi-filter" style={{ 'fontSize': '1em' }}></i>
				</>
			);

		case "multiselect":
			return (
				<>
				<FilterComponentMultiSelect
					filterConfig={filterConfig}
					isEnabled={isEnabled}
					currentFilters={tableState.filters}
					onFilter={onFilter}
					aggregationFields={aggregationFields}
					endpoint={endpoint}
				/>&nbsp;&nbsp;
				<i className="pi pi-filter" style={{ 'fontSize': '1em' }}></i>
				</>
			);
		default:
			return null;
	};
}
