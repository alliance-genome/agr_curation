import React, {useState} from 'react';
import { InputText } from "primereact/inputtext"

export function InputTextEditor({ editorChange, props }) {
    const [selectedValue, setSelectedValue] = useState();
    const onChange = (e) => {
        setSelectedValue(e.value)
        editorChange(props, e)
    }

    return (
        <>
            <InputText
                value={selectedValue}
                onChange={(e) => onChange(e)}
                style={{ width: '100%' }}
            />
        </>
    )
}
