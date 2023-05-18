import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"

export function TrueFalseDropdown({ field, options, showClear = false, editorChange, props }) {
		const [selectedValue, setSelectedValue] = useState(props.rowData[field]);

		const onShow = () => {
			setSelectedValue(props.rowData[field]);
		}

		const onChange = (e) => {
				setSelectedValue(e.value)
				editorChange(props, e)
		}

		return (
				<>
						<Dropdown
								value={selectedValue}
								options={options}
								onShow={onShow}
								onChange={(e) => onChange(e)}
								optionLabel="text"
								showClear={showClear}
								placeholder={selectedValue === null ? '' : JSON.stringify(selectedValue)}
								style={{ width: '100%' }}
						/>
				</>
		)
}
