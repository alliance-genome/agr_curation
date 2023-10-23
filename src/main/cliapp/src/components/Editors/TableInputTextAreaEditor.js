import { InputTextarea } from "primereact/inputtextarea";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useState } from "react";

export const TableInputTextAreaEditor = ({ value, errorMessages, textOnChangeHandler, rowIndex, field, rows, columns }) => {
	const [localValue, setLocalValue] = useState(value);

	const onChange = (e) => {
		setLocalValue(e.target.value);
		textOnChangeHandler(rowIndex, e, field)
	}

	return (
		<>
			<InputTextarea
				id={field}
				value={localValue}
				onChange={onChange}
				style={{ width: '100%' }}
				rows={rows}
				cols={columns}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={field} />
		</>
	);
};