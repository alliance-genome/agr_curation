import React, { useState } from 'react';
import { InputTextarea } from "primereact/inputtextarea"

export const InputTextAreaEditor = ({ rowProps, setRelatedNotesData, relatedNotesRef, fieldName }) => {
  const [fieldValue, setFieldValue] = useState(rowProps.rowData[fieldName] ? rowProps.rowData[fieldName] : '');

  const onChange = (event) => {
    relatedNotesRef.current[rowProps.rowIndex].freeText = event.target.value;
    setFieldValue(event.value);
  }

  return (
    <>
      <InputTextarea
        value={fieldValue}
        onChange={(e) => onChange(e)}
        style={{ width: '100%' }}
      />
    </>
  )
}
