import { useState } from "react";
import { InputText } from 'primereact/inputtext';

export function FilterComponent(props) {
    const [filterValue, setFilterValue] = useState('');
    return (
        <InputText
            disabled={!props.isEnabled}
            value={filterValue}
            onChange={(e) => {
                setFilterValue(e.target.value);
                let filter = {};
                if (e.target.value.length !== 0) {
                    props.fields.forEach((key) => {
                        filter[key] = e.target.value;
                    });
                } else {
                    filter = null;
                }

                const filtersCopy = props.currentFilters;
                if (filter === null) {
                    delete filtersCopy[props.filterName];
                } else {
                    filtersCopy[props.filterName] = filter;
                }
                props.onFilter(filtersCopy);
            }}
        />
    )
}

