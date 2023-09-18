import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { ControlledVocabularyDropdown } from '../ControlledVocabularySelector';
import { useVocabularyTermSetService } from "../../service/useVocabularyTermSetService";

export const VocabularyTermSetEditor = ({
  props,
  onChangeHandler,
  errorMessages,
  rowIndex,
  vocabType,
  field,
  showClear,
  optionLabel="name"
}) => {
  const vocabTerms = useVocabularyTermSetService(vocabType);

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
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={field} />
    </>
  );
};