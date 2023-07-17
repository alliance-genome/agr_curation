import React from 'react';
import { Dropdown } from "primereact/dropdown"

export function ControlledVocabularyFormDropdown({name, value, field, options, editorChange, showClear=false}) {
	return (
		<>
			<Dropdown
				name={name}
				value={value}
				options={options}
				showClear={showClear}
				onChange={(e) => editorChange(e)}
				optionLabel="name"
				style={{ width: '100%' }}
			/>
		</>
	)
}
