import React, {useState} from 'react';
import { MultiSelect } from "primereact/multiselect"

export function ControlledVocabularyMultiSelectDropdown({ options, editorChange, props, placeholderText }) {
    const [selectedValues, setSelectedValues] = useState();
    const onChange = (e) => {
        setSelectedValues(e.value)
        editorChange(props, e)
    }

    return (
        <>
            <MultiSelect
                value={selectedValues}
                options={options}
                onShow={selectedValues}
                onChange={(e) => onChange(e)}
                optionLabel="name"
                placeholder={placeholderText}
                style={{ width: '100%' }}
            />
        </>
    )
}