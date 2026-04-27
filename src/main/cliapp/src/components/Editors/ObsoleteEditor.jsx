import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { TrueFalseDropdown } from '../TrueFalseDropDownSelector';

export const ObsoleteEditor = ({ editorOptions, obsoleteOnChangeHandler, errorMessages, dataKey }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	return (
		<>
			<TrueFalseDropdown
				editorOptions={editorOptions}
				field="obsolete"
				options={booleanTerms?.terms || []}
				editorChange={obsoleteOnChangeHandler}
				showClear={false}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'obsolete'} />
		</>
	);
};
