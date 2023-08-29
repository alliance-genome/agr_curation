import { InputText } from "primereact/inputtext";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";

export const TableInputTextEditor = ({ value, errorMessages, textOnChangeHandler, rowIndex, editorCallback, field }) => {
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