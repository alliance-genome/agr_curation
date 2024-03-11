import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown";

export function NotEditor({ props, value, editorChange }) {
	const [selectedValue, setSelectedValue] = useState(value);
	const textString = value ? "NOT" : "";
	const options = [
		{ label: "NOT", value: true },
		{ label: "null", value: false }
	];

	const onChange = (e) => {
		setSelectedValue(e.value);
		editorChange(e, props);
	}

	return (
		<>
			<Dropdown
				aria-label='dropdown'
				value={selectedValue}
				options={options}
				onChange={(e) => onChange(e)}
				showClear={false}
				placeholder={textString}
				style={{ width: '100%' }}
			/>
		</>
	);
}
