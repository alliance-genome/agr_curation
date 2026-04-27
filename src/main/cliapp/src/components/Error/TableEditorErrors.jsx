import { ErrorMessageComponent } from './ErrorMessageComponent';

export const TableEditorErrors = ({ errorMessagesRef, uiErrorMessagesRef, rowIndex, field }) => {
	return (
		<>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[rowIndex]} errorField={field} />
			{uiErrorMessagesRef && (
				<ErrorMessageComponent errorMessages={uiErrorMessagesRef.current[rowIndex]} errorField={field} />
			)}
		</>
	);
};
