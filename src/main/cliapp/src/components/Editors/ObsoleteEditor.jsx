import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { BooleanDropdown } from './dropdown/boolean/BooleanDropdown';

export const ObsoleteEditor = ({ editorOptions, obsoleteOnChangeHandler, errorMessages, dataKey }) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	return (
		<>
			<BooleanDropdown
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
