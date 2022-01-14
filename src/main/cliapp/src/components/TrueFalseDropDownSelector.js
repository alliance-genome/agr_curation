import React, {useState} from 'react';
import { Dropdown } from "primereact/dropdown"

export function TrueFalseDropdown({ options, editorChange, props }) {
    const [selectedValue, setSelectedValue] = useState();
    const onChange = (e) => {
        setSelectedValue(e.value)
        editorChange(props, e)
    }

    return (
        <>
            <Dropdown
                value={selectedValue}
                options={options}
                onChange={(e) => onChange(e)}
                optionLabel="text"
                optionValue="name"
                placeholder={JSON.stringify(props.rowData[props.field])}
                style={{ width: '100%' }}
            />
        </>
    )
}
