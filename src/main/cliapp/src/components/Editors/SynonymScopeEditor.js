import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";
import { useState } from "react";

export const SynonymScopeEditor = ({ value, synonymScopeOnChangeHandler, errorMessages, rowIndex }) => {
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');
	const [localValue, setLocalValue] = useState(value); 

	const onChange = (e) => {
		setLocalValue(e.target.value);
    synonymScopeOnChangeHandler(e, rowIndex, "synonymScope")
	}

  return (
    <>
      <Dropdown
        field="synonymScope"
        value={localValue}
        options={synonymScopeTerms}
        onChange={onChange}
        optionLabel="name"
        showClear={true}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"synonymScope"} />
    </>
  );
};