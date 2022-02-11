import { useEffect, useState } from "react";
import { MultiSelect } from 'primereact/multiselect';
import { useQuery } from 'react-query';
import { SearchService } from '../service/SearchService';

export function FilterMultiSelectComponent({ aggregationFields, sortMapping, tableState, isEnabled, field, tokenOperator, filterName, currentFilters, onFilter }) {
  //const [filterValue, setFilterValue] = useState({});
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [selectableOptions, setSelectableOptions] = useState([]);
  const [tableAggregations, setTableAggregations] = useState([]);

  const searchService = new SearchService();
  useQuery(['diseaseAnnotations', tableState],
    () => searchService.search('disease-annotation', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, aggregationFields), {
    onSuccess: (data) => {
      console.log(data);
      
      let tmp = [];
      for (let key in data.aggregations[field]) {
        tmp.push({
          label: key,
          value: data.aggregations[field][key]
        });
      };

      setSelectableOptions({ diseaseRelations: tmp });
      setTableAggregations(tmp);
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

  return (
    <MultiSelect
      disabled={!isEnabled}
      value={selectedOptions}
      options={tableAggregations ? tableAggregations : []}
      placeholder="Select"
      display="chip"
      style={{ width: '100%' }}
      filter className={"multiselect-custom"}
      panelFooterTemplate={panelFooterTemplate}
      onChange={(e) => {
        let filter = {};
        setSelectedOptions(e.value);
        /* if(e.target.value && e.target.value.length !== 0) {
             let delim = '';
             filter[field] = {
                 queryString : '',
                 tokenOperator : tokenOperator
             };
             for(let i in e.target.value) {
                 filter[field]["queryString"] += delim + e.target.value[i].text;
                 delim = ' ';
             }
             setFilterValue(filter);
         } else {
             filter = null;
         }*/
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

