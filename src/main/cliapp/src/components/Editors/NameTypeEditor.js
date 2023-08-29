import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const NameTypeEditor = ({ value, nameTypeOnChangeHandler, errorMessages, rowIndex, editorCallback }) => {
	const synonymTypeTerms = useControlledVocabularyService('name_type');

  return (
    <>
      <Dropdown
        field="nameType"
        value={value}
        options={synonymTypeTerms}
        onChange={(e) => nameTypeOnChangeHandler(e, editorCallback, rowIndex, "nameType")}
        optionLabel="name"
        showClear={false}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"nameType"} />
    </>
  );
};