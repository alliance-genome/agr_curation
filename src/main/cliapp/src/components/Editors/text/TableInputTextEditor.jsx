import { InputText } from 'primereact/inputtext';
import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';
import { useState } from 'react';

export const TableInputTextEditor = ({
	value,
	errorMessages,
	dataKey,
	textOnChangeHandler,
	editorCallback,
	rowIndex,
	field,
}) => {
	const [localValue, setLocalValue] = useState(value ?? '');

	const onChange = (e) => {
		setLocalValue(e.target.value);
		textOnChangeHandler(rowIndex, e, field);
		if (editorCallback) {
			editorCallback(e.target.value);
		}
	};

	return (
		<>
			<InputText id={field} value={localValue} onChange={onChange} />
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={field} />
		</>
	);
};
