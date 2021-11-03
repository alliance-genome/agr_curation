import { useState } from "react";
import { InputText } from 'primereact/inputtext';

export function FilterComponent(props){
        const [filterValue, setFilterValue] = useState('');
        return (
            <InputText 
                disabled={!props.isEnabled}
                value={filterValue} 
                onChange={(e) => {
                    setFilterValue(e.target.value);
                    const filter = {};
                    filter[props.field] = {
                        value: e.target.value,
                        matchMode: "startsWith"
                    }
                    props.onFilter(filter, props.field);
                }
                }
            />
        )
    }


    function buildFilter(value, field){//put in utils file?
        const filter = {};
        filter[field] = {
            value: value,
            matchMode: "startsWith"
        }
        return filter;
    }
