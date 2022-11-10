import React from 'react';
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyFormDropdown({name, value, field, options, editorChange}) {
	return (
		<>
			<Dropdown
				name={name}
				value={value}
				options={options}
				onChange={(e) => editorChange(e)}
				optionLabel="name"
				style={{ width: '100%' }}
			/>
		</>
	)
}
