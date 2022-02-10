import {useEffect, useState} from "react";
import { MultiSelect } from 'primereact/multiselect';

export function FilterMultiSelectComponent({ isEnabled, field, tokenOperator ,filterName, currentFilters, onFilter, tableAggregations }) {
    //const [filterValue, setFilterValue] = useState({});
    const [selectedOptions, setSelectedOptions] = useState([]);
    const [selectableOptions, setSelectableOptions]  = useState([]);

    const panelFooterTemplate = () => {
        const length = selectedOptions ? selectedOptions.length : 0;
        return (
            <div style={{padding:'0.9rem'}}>
                <b>{length}</b> item{length > 1 ? 's' : ''} selected.
            </div>
        );
    };

   useEffect(() => {
        console.log(tableAggregations);
        if(tableAggregations && tableAggregations[field])
            setSelectableOptions(tableAggregations[field]);
      /* if(currentFilters[filterName]){
            for (let i = 0; i < options.length; i++) {
                if (currentFilters[filterName] && options[i][optionField] == currentFilters[filterName][field].queryString) {
                    setFilterValue(options[i]);
                }
            }
        }else {
            setFilterValue(null);
        }*/
    }, [tableAggregations,field]);

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

