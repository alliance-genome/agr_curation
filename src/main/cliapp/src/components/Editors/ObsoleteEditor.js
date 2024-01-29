import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";
import { TrueFalseDropdown } from "../TrueFalseDropDownSelector";

export const ObsoleteEditor = ({ props, obsoleteOnChangeHandler, errorMessages, dataKey }) => {
  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");

  return (
    <>
      <TrueFalseDropdown
        props={props}
        field="obsolete"
        options={booleanTerms}
        editorChange={obsoleteOnChangeHandler}
        showClear={false}
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={"obsolete"} />
    </>
  );
};