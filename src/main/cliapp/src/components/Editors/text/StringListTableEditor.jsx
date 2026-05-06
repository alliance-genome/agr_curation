import React from 'react';
import { StringListEditor } from './StringListEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const StringListTableEditor = ({ editorOptions, field, errorMessagesRef }) => {
	return (
		<>
			<StringListEditor editorOptions={editorOptions} fieldName={field} />
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
