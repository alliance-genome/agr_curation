import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';
import { ControlledVocabularyDropdown } from '../dropdown/vocabulary/ControlledVocabularyDropdown';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';

export const VocabularyTermSetEditor = ({
	editorOptions,
	onChangeHandler,
	errorMessages,
	dataKey,
	vocabType,
	field,
	showClear,
	optionLabel = 'name',
	placeholder,
}) => {
	const vocabTerms = useVocabularyTermSetService(vocabType);

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
				placeholderText={placeholder || ''}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={field} />
		</>
	);
};
