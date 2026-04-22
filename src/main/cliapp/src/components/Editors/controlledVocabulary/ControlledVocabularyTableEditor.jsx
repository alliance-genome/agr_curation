import { ControlledVocabularyDropdown } from '../../ControlledVocabularySelector';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';

export const ControlledVocabularyTableEditor = ({
	editorOptions,
	field,
	options,
	showClear = false,
	errorMessagesRef,
	dataKey,
	placeholderField = 'name',
}) => {
	return (
		<>
			<ControlledVocabularyDropdown
				field={field}
				options={options}
				editorChange={(editorOptions, event) => editorOptions.editorCallback(event.value)}
				editorOptions={editorOptions}
				showClear={showClear}
				placeholderText={editorOptions.rowData[field]?.[placeholderField]}
				dataKey={dataKey}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
