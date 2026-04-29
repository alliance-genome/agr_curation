import { BooleanDropdown } from './BooleanDropdown';
import { ErrorMessageComponent } from '../../../Error/ErrorMessageComponent';
import { useControlledVocabularyService } from '../../../../service/useControlledVocabularyService';

export const BooleanTableEditor = ({ editorOptions, errorMessagesRef, field, showClear = false }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	return (
		<>
			<BooleanDropdown
				options={booleanTerms?.terms || []}
				editorChange={(editorOptions, event) => {
					if (event.value && event.value !== '') {
						editorOptions.editorCallback(JSON.parse(event.value.name));
					} else {
						editorOptions.editorCallback(null);
					}
				}}
				editorOptions={editorOptions}
				field={field}
				showClear={showClear}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField={field} />
		</>
	);
};
