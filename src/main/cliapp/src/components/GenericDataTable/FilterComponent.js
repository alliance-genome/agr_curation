import React from 'react';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { FilterComponentDropDown } from '../../components/FilterComponentDropdown';
import { FilterMultiSelectComponent } from '../../components/FilterMultiSelectComponent';

export const FilterComponent = ({
  type,
  isEnabled,
  fields,
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
        <FilterComponentInputText
          isEnabled={isEnabled}
          fields={fields}
          filterName={filterName}
          currentFilters={tableState.filters}
          onFilter={onFilter}
        />
      );

    case "dropdown":
      return (
        <FilterComponentDropDown
          isEnabled={isEnabled}
          field={fields[0]}
          filterName={filterName}
          currentFilters={tableState.filters}
          onFilter={onFilter}
          options={options}
          optionField={optionField}
        />
      );

    case "multiselect":
      return (
        <FilterMultiSelectComponent
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
        />
      );
    default:
      return null;
  };
}
