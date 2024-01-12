import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";
import { ControlledVocabularyDropdown } from '../ControlledVocabularySelector';

export const ControlledVocabularyEditor = ({
  props,
  onChangeHandler,
  errorMessages,
  dataKey,
  vocabType,
  field,
  showClear,
  optionLabel="name"
}) => {
  const vocabTerms = useControlledVocabularyService(vocabType);

  return (
    <>
      <ControlledVocabularyDropdown
        field={field}
        options={vocabTerms}
        editorChange={onChangeHandler}
        props={props}
        showClear={showClear}
        optionLabel={optionLabel}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={field} />
    </>
  );
};