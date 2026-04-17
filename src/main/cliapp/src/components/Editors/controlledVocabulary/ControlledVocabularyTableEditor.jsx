import { ControlledVocabularyDropdown } from '../../ControlledVocabularySelector';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const ControlledVocabularyTableEditor = ({
	editorOptions,
	field,
	options,
	showClear = true,
	errorMessagesRef,
}) => {
	return (
		<>
			<ControlledVocabularyDropdown
				field={field}
				options={options}
				editorChange={(editorOptions, event) => editorOptions.editorCallback(event.value)}
				editorOptions={editorOptions}
				showClear={showClear}
				placeholderText={editorOptions.rowData[field]?.name}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
