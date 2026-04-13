import React, { useState } from 'react';
import { MultiSelect } from 'primereact/multiselect';

export function ControlledVocabularyMultiSelectDropdown({ options, editorChange, editorOptions, placeholderText }) {
	const [selectedValues, setSelectedValues] = useState(editorOptions.rowData.diseaseQualifiers);
	const onChange = (e) => {
		setSelectedValues(e.value);
		editorChange(editorOptions, e);
	};

	return (
		<>
			<MultiSelect
				value={selectedValues}
				options={options}
				onChange={(e) => onChange(e)}
				display="chip"
				optionLabel="name"
				placeholder={placeholderText}
				style={{ width: '100%' }}
			/>
		</>
	);
}
