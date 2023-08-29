import React from 'react'
import { EllipsisTableCell } from '../EllipsisTableCell';
import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { InputText } from "primereact/inputtext"
import { InputTextEditor } from '../InputTextEditor';

export const synonymUrlEditorTemplate = (props, errorMessages) => {
	return (
		<>
			<InputTextEditor
				rowProps={props}
				fieldName={"synonymUrl"}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"synonymUrl"} />
		</>
	);
};

export const DisplayTextEditor = ({ value, errorMessages, textOnChangeHandler, rowIndex, editorCallback }) => {
	const field = "displayText";
	return (
		<>
			<InputText
				id={field}
				value={value}
				onChange={(event) => textOnChangeHandler(rowIndex, event, editorCallback, field)}
				fieldName={field}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={field} />
		</>
	);
};


export const FormatTextEditor = ({ value, errorMessages, textOnChangeHandler, rowIndex, editorCallback }) => {
	const field = "formatText";
	return (
		<>
			<InputText
				id={field}
				value={value}
				onChange={(event) => textOnChangeHandler(rowIndex, event, editorCallback, field)}
				fieldName={field}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={field} />
		</>
	);
};
