import React from 'react';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { FilterComponentDropDown } from '../../components/FilterComponentDropdown';
import { FilterMultiSelectComponent } from '../../components/FilterMultiSelectComponent';

export const FilterComponent = ({
	type,
	isEnabled,
	fields,
	nonNullFields,
	nullFields,
	filterName,
	onFilter,
	options,
	optionField,
	useKeywordFields = false,
	aggregationFields,
	tableState,
	annotationsAggregations,
	endpoint,
}) => {

	switch(type){
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
				<FilterComponentDropDown
					isEnabled={isEnabled}
					field={fields[0]}
					nonNullFields={nonNullFields}
					nullFields={nullFields}
					filterName={filterName}
					currentFilters={tableState.filters}
					onFilter={onFilter}
					options={options}
					optionField={optionField}
				/>&nbsp;&nbsp;
				<i className="pi pi-filter" style={{ 'fontSize': '1em' }}></i>
				</>
			);

		case "multiselect":
			return (
				<>
				<FilterMultiSelectComponent
					isEnabled={isEnabled}
					field={fields[0]}
					nonNullFields={nonNullFields}
					nullFields={nullFields}
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
