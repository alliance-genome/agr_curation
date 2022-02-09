import { useState } from "react";
import { MultiSelect } from 'primereact/multiselect';

export function FilterMultiSelectComponent({ isEnabled, field, tokenOperator ,filterName, currentFilters, onFilter, options }) {
    const [filterValue, setFilterValue] = useState(null);
    const [selectedValues, setSelectedValues] = useState(null);
    const panelFooterTemplate = () => {
        const selectedItems = selectedValues;
        const length = selectedItems ? selectedItems.length : 0;
        return (
            <div style={{padding:'0.9rem'}}>
                <b>{length}</b> item{length > 1 ? 's' : ''} selected.
            </div>
        );
    };

    return (
        <MultiSelect
            disabled={!isEnabled}
            value={selectedValues}
            options={options}
            optionLabel="text"
            placeholder="Select"
            display="chip"
            style={{ width: '100%' }}
            filter className={"multiselect-custom"}
            panelFooterTemplate={panelFooterTemplate}
            onChange={(e) => {
                let filter = {};
                setSelectedValues(e.value);
                if(e.target.value && e.target.value.length !== 0) {
                    let delim = '';
                    filter[field] = {
                        queryString : '',
                        tokenOperator : tokenOperator
                    };
                    for(let i in e.target.value) {
                        filter[field]["queryString"] += delim + e.target.value[i].text;
                        delim = ' ';
                    }
                    //filter[field]["tokenOperator"] = tokenOperator;
                    setFilterValue(filter);
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

