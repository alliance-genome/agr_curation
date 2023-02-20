import React, { useState } from 'react';
import { InputText } from "primereact/inputtext"

export function InputTextEditor({ rowProps, fieldName }) {
	const [fieldValue, setFieldValue] = useState(rowProps.rowData[fieldName] ? rowProps.rowData[fieldName] : '');

	const editorChange = (event) => {
		let updatedConditions = [...rowProps.props.value];
		if (event.target.value || event.target.value === '') {
			updatedConditions[rowProps.rowIndex][fieldName] = event.target.value;
			setFieldValue(updatedConditions[rowProps.rowIndex][fieldName]);
		}
	}
	const onChange = (e) => {
		// setSelectedValue(e.value)
		editorChange(e)
	}

	return (
		<>
			<InputText
				aria-label={fieldName}
				value={fieldValue}
				onChange={(e) => onChange(e)}
				style={{ width: '100%' }}
			/>
		</>
	)
}
