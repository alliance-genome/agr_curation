import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const SynonymScopeEditor = ({ value, synonymScopeOnChangeHandler, errorMessages, rowIndex, editorCallback }) => {
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');

  return (
    <>
      <Dropdown
        field="synonymScope"
        value={value}
        options={synonymScopeTerms}
        onChange={(e) => synonymScopeOnChangeHandler(e, editorCallback, rowIndex, "synonymScope")}
        optionLabel="name"
        showClear={true}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"synonymScope"} />
    </>
  );
};