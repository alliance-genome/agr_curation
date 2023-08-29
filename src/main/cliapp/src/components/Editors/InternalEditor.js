import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const InternalEditor = ({ value, internalOnChangeHandler, errorMessages, rowIndex, editorCallback }) => {
  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");

  return (
    <>
      <Dropdown
        field="internal"
        value={value}
        options={booleanTerms}
        onChange={(e) => internalOnChangeHandler(e, editorCallback, rowIndex, "internal")}
        optionLabel="text"
        showClear={false}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"internal"} />
    </>
  );
};