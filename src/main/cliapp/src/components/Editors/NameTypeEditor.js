import { useState } from "react";
import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const NameTypeEditor = ({ value, nameTypeOnChangeHandler, errorMessages, rowIndex }) => {
	const synonymTypeTerms = useControlledVocabularyService('name_type');
	const [localValue, setLocalValue] = useState(value); 

	const onChange = (e) => {
		setLocalValue(e.target.value);
    nameTypeOnChangeHandler(e, rowIndex, "nameType");
	}

  return (
    <>
      <Dropdown
        field="nameType"
        value={localValue}
        options={synonymTypeTerms}
        onChange={onChange}
        optionLabel="name"
        showClear={false}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"nameType"} />
    </>
  );
};