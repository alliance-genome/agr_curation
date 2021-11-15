import React, {useState} from 'react';
import { Message } from "primereact/message"
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyDropdown({options, editorChange, props}) {
    const [selectedValue, setSelectedValue] = useState()
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
                placeholder="Select Term"
                style={{ width: '100%' }}
            />
            <Message severity={props.rowData.object.errorSeverity ? props.rowData.object.errorSeverity : ""} text={props.rowData.object.errorMessage} />
        </>
    )
}