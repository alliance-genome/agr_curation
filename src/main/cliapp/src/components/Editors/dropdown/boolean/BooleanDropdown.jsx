import { useState } from 'react';
import { Dropdown } from 'primereact/dropdown';

export function BooleanDropdown({ field, options, showClear = false, editorChange, editorOptions }) {
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
				value={selectedValue}
				options={options || []}
				onShow={onShow}
				onChange={(e) => onChange(e)}
				optionLabel="text"
				showClear={showClear}
				placeholder={selectedValue === null ? '' : JSON.stringify(selectedValue)}
				style={{ width: '100%' }}
			/>
		</>
	);
}
