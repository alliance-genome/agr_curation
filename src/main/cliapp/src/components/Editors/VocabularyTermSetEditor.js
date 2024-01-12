import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { ControlledVocabularyDropdown } from '../ControlledVocabularySelector';
import { useVocabularyTermSetService } from "../../service/useVocabularyTermSetService";

export const VocabularyTermSetEditor = ({
  props,
  onChangeHandler,
  errorMessages,
  dataKey,
  vocabType,
  field,
  showClear,
  optionLabel="name",
  placeholder
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
        placeholderText={placeholder || ""}
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={field} />
    </>
  );
};