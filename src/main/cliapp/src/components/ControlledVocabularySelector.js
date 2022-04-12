import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyDropdown({ field, options, editorChange, props, showClear, placeholderText}) {
    const [selectedValue, setSelectedValue] = useState(props.rowData[field]);
    const onShow = () => {
        setSelectedValue(props.rowData[field])
    }
    const onChange = (e) => {
        setSelectedValue(e.value)
        editorChange(props, e)
    }
  
    return (
        <>
            <Dropdown
                value={selectedValue}
                options={options}
                onShow={onShow}
                onChange={(e) => onChange(e)}
                optionLabel="name"
                showClear={showClear}
                placeholder={placeholderText}
                style={{ width: '100%' }}
            />
        </>
    )
}
