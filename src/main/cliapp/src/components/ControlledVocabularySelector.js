import React, {useState} from 'react';
import { Message } from "primereact/message"
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyDropdown({ options, editorChange, props }) {
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
                optionLabel="name"
                placeholder={props.rowData[props.field]}
                style={{ width: '100%' }}
            />
        </>
    )
}