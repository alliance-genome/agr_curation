import React, { useState } from 'react';
import { Dropdown } from 'primereact/dropdown';

export function ControlledVocabularyDropdown({
	field,
	options,
	editorChange,
	editorOptions,
	showClear,
	placeholderText,
	dataKey,
}) {
	const [selectedValue, setSelectedValue] = useState(editorOptions.rowData[field]);
	const onShow = () => {
		setSelectedValue(editorOptions.rowData[field]);
	};
	const onChange = (e) => {
		setSelectedValue(e.value);
		editorChange(editorOptions, e);
	};

	return (
		<>
			<Dropdown
				ariaLabel={field}
				value={selectedValue}
				dataKey={dataKey}
				options={options}
				onShow={onShow}
				onChange={(e) => onChange(e)}
				optionLabel="name"
				showClear={showClear}
				placeholder={placeholderText}
				style={{ width: '100%' }}
			/>
		</>
	);
}
