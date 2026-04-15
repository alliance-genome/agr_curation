import React, { useState } from 'react';
import { Dropdown } from 'primereact/dropdown';

export function NotEditor({ value, editorChange }) {
	const [selectedValue, setSelectedValue] = useState(value);
	const textString = selectedValue ? 'NOT' : '';
	const options = [{ label: 'NOT', value: true }];

	const onChange = (e) => {
		const newValue = e.value == null ? false : e.target.value;
		setSelectedValue(newValue);
		editorChange(newValue);
	};

	return (
		<>
			<Dropdown
				aria-label="dropdown"
				name="negated"
				value={selectedValue}
				options={options}
				onChange={(e) => onChange(e)}
				showClear={true}
				placeholder={textString}
				style={{ width: '100%' }}
			/>
		</>
	);
}
