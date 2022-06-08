import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"

export function TrueFalseDropdown({ field, options, editorChange, props }) {
		const [selectedValue, setSelectedValue] = useState(props.rowData[field]);

		const onShow = () => {
				setSelectedValue(props.rowData[field])
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
								placeholder={JSON.stringify(props.rowData[field])}
								style={{ width: '100%' }}
						/>
				</>
		)
}
