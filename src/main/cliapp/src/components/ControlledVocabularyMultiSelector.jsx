import React, { useState } from 'react';
import { MultiSelect } from 'primereact/multiselect';

export function ControlledVocabularyMultiSelectDropdown({ field, options, editorChange, editorOptions, placeholderText }) {
	const [selectedValues, setSelectedValues] = useState(editorOptions.rowData[field]);

	const onShow = () => {
		setSelectedValues(editorOptions.rowData[field]);
	};

	const onChange = (e) => {
		setSelectedValues(e.value);
		editorChange(editorOptions, e);
	};

	return (
		<>
			<MultiSelect
				value={selectedValues}
				options={options}
				onShow={onShow}
				onChange={(e) => onChange(e)}
				display="chip"
				optionLabel="name"
				placeholder={placeholderText}
				style={{ width: '100%' }}
			/>
		</>
	);
}
