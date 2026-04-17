import React, { useState } from 'react';
import { InputTextarea } from 'primereact/inputtextarea';

export function StringListTextAreaEditor({ rowProps, fieldName, rows = 5 }) {
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
			<InputTextarea
				aria-label={fieldName}
				value={fieldValue}
				onChange={onChange}
				style={{ width: '100%' }}
				rows={rows}
				autoResize
			/>
		</>
	);
}
