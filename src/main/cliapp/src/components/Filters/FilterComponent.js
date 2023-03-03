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

	switch(filterConfig.type) {
		case "input":
			return (
				<>
				<FilterComponentInputText
					isEnabled={isEnabled}
					fields={fields}
					nonNullFields={nonNullFields}
					nullFields={nullFields}
					filterName={filterName}
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
					isEnabled={isEnabled}
					field={fields[0]}
					filterName={filterName}
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
					isEnabled={isEnabled}
					field={fields[0]}
					useKeywordFields={useKeywordFields}
					filterName={filterName}
					currentFilters={tableState.filters}
					onFilter={onFilter}
					aggregationFields={aggregationFields}
					tableState={tableState}
					annotationsAggregations={annotationsAggregations}
					endpoint={endpoint}
				/>&nbsp;&nbsp;
				<i className="pi pi-filter" style={{ 'fontSize': '1em' }}></i>
				</>
			);
		default:
			return null;
	};
}
