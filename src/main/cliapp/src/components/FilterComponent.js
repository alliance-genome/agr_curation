import { useState } from "react";
import { InputText } from 'primereact/inputtext';

export function FilterComponent({ isEnabled, fields, filterName, currentFilters, onFilter }) {
    const [filterValue, setFilterValue] = useState('');
    return (
        <InputText
            disabled={!isEnabled}
            value={filterValue}
            onChange={(e) => {
                setFilterValue(e.target.value);
                let filter = {};
                if (e.target.value.length !== 0) {
                    fields.forEach((key) => {
                        filter[key] = e.target.value;
                    });
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

