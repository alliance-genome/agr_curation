import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';
import { DialogErrorMessageComponent } from './DialogErrorMessageComponent';
import { InputTextEditor } from './InputTextEditor';

export const synonymScopeTemplate = (rowData) => {
	return <EllipsisTableCell>{rowData.synonymScope?.name}</EllipsisTableCell>;
};

export const nameTypeTemplate = (rowData) => {
	return <EllipsisTableCell>{rowData.nameType?.name}</EllipsisTableCell>;
};

export const synonymUrlTemplate = (rowData) => {
	return <EllipsisTableCell>{rowData.synonymUrl}</EllipsisTableCell>;
};

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

export const displayTextTemplate = (rowData) => {
	return <EllipsisTableCell>{rowData.displayText}</EllipsisTableCell>;
};

export const displayTextEditorTemplate = (props, errorMessages) => {
	return (
		<>
			<InputTextEditor
				rowProps={props}
				fieldName={"displayText"}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"displayText"} />
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
