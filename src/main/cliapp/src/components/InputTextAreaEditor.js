import React, { useState } from 'react';
import { InputTextarea } from "primereact/inputtextarea";

export const InputTextAreaEditor = ({ initalValue, editorChange, rows, columns}) => {
  const [fieldValue, setFieldValue] = useState(initalValue ? initalValue : '');

  const onChange = (event) => {
    setFieldValue(event.value);
    editorChange(event);
  }

  return (
    <>
      <InputTextarea
        value={fieldValue}
        onChange={(e) => onChange(e)}
        style={{ width: '100%' }}
        rows={rows}
        cols={columns}
      />
    </>
  )
}
