import {useState} from "react";
import { MultiSelect } from 'primereact/multiselect';
import { useQuery } from 'react-query';
import { SearchService } from '../service/SearchService';

export function FilterMultiSelectComponent({ isEnabled, field, tokenOperator, filterName, currentFilters, onFilter, aggregationFields }) {
    const [selectedOptions, setSelectedOptions] = useState([]);
  const [selectableOptions, setSelectableOptions] = useState([]);

  const searchService = new SearchService();
  useQuery(['diseaseAnnotationsAggregations', aggregationFields],
    () => searchService.search('disease-annotation', 0, 0, null,{},{}, aggregationFields), {
     onSuccess: (data) => {
         let tmp = [];
         if(data.aggregations){
             for (let key in data.aggregations[field]) {
                 tmp.push({
                     optionLabel: key,
                     optionValue: data.aggregations[field][key]
                 });
             }
         };
      setSelectableOptions(tmp);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const panelFooterTemplate = () => {
    const length = selectedOptions ? selectedOptions.length : 0;
    return (
      <div style={{ padding: '0.9rem' }}>
        <b>{length}</b> item{length > 1 ? 's' : ''} selected.
      </div>
    );
  };

  const itemTemplate = (option) => {
    return (<div>{option.optionLabel} ({option.optionValue})</div>);
  };

  return (
    <MultiSelect
      disabled={!isEnabled}
      value={selectedOptions}
      options={selectableOptions}
      placeholder="Select"
      display="chip"
      optionLabel="optionLabel"
      style={{ width: '100%' }}
      itemTemplate={itemTemplate}
      filter className={"multiselect-custom"}
      panelFooterTemplate={panelFooterTemplate}
      onChange={(e) => {
          console.log(e.target.value);
          setSelectedOptions(e.target.value);
          let filter = {};
          let queryString = '';
          let delim = "";
          if (e.target.value && e.target.value.length !== 0) {
              for(let i in e.target.value) {
                  queryString += delim + e.target.value[i].optionLabel;
                  delim = " ";
              }
              filter[field] = {
                  queryString: queryString,
                  tokenOperator: tokenOperator
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

