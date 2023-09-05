import { InputText } from "primereact/inputtext";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useState } from "react";

export const TableInputTextEditor = ({ value, errorMessages, textOnChangeHandler, rowIndex, field }) => {
	const [localValue, setLocalValue] = useState(value);

	const onChange = (e) => {
		setLocalValue(e.target.value);
		textOnChangeHandler(rowIndex, e, field)
	}

	return (
		<>
			<InputText
				id={field}
				value={localValue}
				onChange={onChange}
				fieldName={field}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={field} />
		</>
	);
};