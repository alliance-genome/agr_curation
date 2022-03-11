import React, {useState} from 'react';
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyDropdown({ options, editorChange, props, placeholderText }) {
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
                placeholder={placeholderText}
                style={{ width: '100%' }}
            />
        </>
    )
}
