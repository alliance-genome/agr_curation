import { ControlledVocabularyMultiSelectDropdown } from '../../ControlledVocabularyMultiSelector';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const ControlledVocabularyMultiSelectTableEditor = ({ editorOptions, field, options, errorMessagesRef }) => {
	const values = editorOptions.rowData[field];
	const placeholderText = values ? values.map((x) => x.name).join() : '';

	return (
		<>
			<ControlledVocabularyMultiSelectDropdown
				field={field}
				options={options}
				editorChange={(editorOptions, event) => editorOptions.editorCallback(event.value)}
				editorOptions={editorOptions}
				placeholderText={placeholderText}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
