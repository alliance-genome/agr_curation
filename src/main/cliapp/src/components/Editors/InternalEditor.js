import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";
import { TrueFalseDropdown } from "../TrueFalseDropDownSelector";

export const InternalEditor = ({ props, internalOnChangeHandler, errorMessages, rowIndex }) => {
  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");

  return (
    <>
      <TrueFalseDropdown
        props={props}
        field="internal"
        options={booleanTerms}
        editorChange={internalOnChangeHandler}
        showClear={false}
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"internal"} />
    </>
  );
};