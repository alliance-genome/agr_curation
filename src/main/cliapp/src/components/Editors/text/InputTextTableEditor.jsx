import React from 'react';
import { InputTextEditor } from '../../InputTextEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const InputTextTableEditor = ({ editorOptions, field, errorMessagesRef }) => {
	return (
		<>
			<InputTextEditor editorOptions={editorOptions} fieldName={field} />
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
