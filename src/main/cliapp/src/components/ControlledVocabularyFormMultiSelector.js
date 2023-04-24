import React from 'react';
import { MultiSelect } from "primereact/multiselect"

export function ControlledVocabularyFormMultiSelectDropdown({ name, value, options, editorChange, placeholderText, style }) {
	return (
		<>
			<MultiSelect
				name={name}
				value={value}
				options={options}
				onChange={(e) => editorChange(e)}
				display="chip"
				optionLabel="name"
				placeholder={placeholderText}
				style={style ? style : {width: '100%'} }
			/>
		</>
	)
}
