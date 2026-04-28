import React from 'react';
import { StringListTextAreaEditor } from '../StringListTextAreaEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const StringListTextAreaTableEditor = ({ editorOptions, field, errorMessagesRef, rows = 5 }) => {
	return (
		<>
			<StringListTextAreaEditor editorOptions={editorOptions} fieldName={field} rows={rows} />
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
