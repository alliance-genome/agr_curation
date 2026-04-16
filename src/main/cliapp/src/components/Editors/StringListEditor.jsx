import React, { useState } from 'react';
import { InputText } from 'primereact/inputtext';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';

export function StringListEditor({ rowProps, fieldName }) {
	const initialValue = Array.isArray(rowProps.rowData[fieldName]) ? rowProps.rowData[fieldName].join(', ') : '';
	const [fieldValue, setFieldValue] = useState(initialValue);

	const onChange = (e) => {
		const value = e.target.value;
		setFieldValue(value);
		let updatedEntities = [...rowProps.props.value];
		updatedEntities[rowProps.rowIndex][fieldName] = value
			? value
					.split(',')
					.map((s) => s.trim())
					.filter((s) => s.length > 0)
			: [];
	};

	return (
		<>
			<InputText aria-label={fieldName} value={fieldValue} onChange={onChange} style={{ width: '100%' }} />
		</>
	);
}
