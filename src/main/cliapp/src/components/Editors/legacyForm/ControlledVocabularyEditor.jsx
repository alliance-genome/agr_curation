import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../dropdown/vocabulary/ControlledVocabularyDropdown';

export const ControlledVocabularyEditor = ({
	editorOptions,
	onChangeHandler,
	errorMessages,
	dataKey,
	vocabType,
	field,
	showClear,
	optionLabel = 'name',
}) => {
	const vocabTerms = useControlledVocabularyService(vocabType);

	return (
		<>
			<ControlledVocabularyDropdown
				field={field}
				options={vocabTerms}
				editorChange={onChangeHandler}
				editorOptions={editorOptions}
				showClear={showClear}
				optionLabel={optionLabel}
				dataKey="id"
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={field} />
		</>
	);
};
