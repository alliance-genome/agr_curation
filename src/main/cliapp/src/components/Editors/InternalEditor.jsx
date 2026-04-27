import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { TrueFalseDropdown } from '../TrueFalseDropDownSelector';

export const InternalEditor = ({ editorOptions, internalOnChangeHandler, errorMessages, dataKey }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	return (
		<>
			<TrueFalseDropdown
				editorOptions={editorOptions}
				field="internal"
				options={booleanTerms?.terms || []}
				editorChange={internalOnChangeHandler}
				showClear={false}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'internal'} />
		</>
	);
};
