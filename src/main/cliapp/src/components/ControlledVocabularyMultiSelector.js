import React, {useState} from 'react';
import { MultiSelect } from "primereact/multiselect"

export function ControlledVocabularyMultiSelectDropdown({ options, editorChange, props, placeholderText }) {
    const [selectedValues, setSelectedValues] = useState(props.rowData.diseaseQualifiers);
    const onChange = (e) => {
        setSelectedValues(e.value)
        editorChange(props, e)
    }
    
    return (
        <>
            <MultiSelect
                value={selectedValues}
                options={options}
                onChange={(e) => onChange(e)}
                display="chip"
                optionLabel="name"
                placeholder={placeholderText}
                style={{ width: '100%' }}
            />
        </>
    )
}