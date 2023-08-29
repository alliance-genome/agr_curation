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

export const DisplayTextEditor = ({ value, errorMessages, displayTextOnChangeHandler, rowIndex, editorCallback }) => {
	return (
		<>
			<InputText
				id="displayText"
				value={value}
				onChange={(event) => displayTextOnChangeHandler(rowIndex, event, editorCallback)}
				fieldName={"displayText"}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"displayText"} />
		</>
	);
};

export const formatTextTemplate = (rowData) => {
	return <EllipsisTableCell>{rowData.formatText}</EllipsisTableCell>;
};

export const formatTextEditorTemplate = (props, errorMessages) => {
	return (
		<>
			<InputTextEditor
				rowProps={props}
				fieldName={"formatText"}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"formatText"} />
		</>
	);
};
