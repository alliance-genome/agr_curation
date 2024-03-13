import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown";

export function NotEditor({ props, value, editorChange }) {
	const [selectedValue, setSelectedValue] = useState(value);
	const textString = value ? "NOT" : "";
	const options = [
		{ label: "NOT", value: true },
	];

	const onChange = (e) => {
		let event;
		if(e.value === undefined){
			event = {
				target: {
					value: false,
					name: e.target.name
				}
			}
		} else {
			event = e;
		}
		setSelectedValue(event.target.value);
		editorChange(event, props);
	}

	return (
		<>
			<Dropdown
				aria-label='dropdown'
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
