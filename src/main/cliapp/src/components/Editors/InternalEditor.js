import { useState } from "react";
import { Dropdown } from "primereact/dropdown";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { useControlledVocabularyService } from "../../service/useControlledVocabularyService";

export const InternalEditor = ({ value, internalOnChangeHandler, errorMessages, rowIndex }) => {
  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");
	const [localValue, setLocalValue] = useState(value); 

	const onChange = (e) => {
		setLocalValue(e.target.value);
    internalOnChangeHandler(e, rowIndex, "internal")
	}

  return (
    <>
      <Dropdown
        field="internal"
        value={localValue}
        options={booleanTerms}
        onChange={onChange}
        optionLabel="text"
        showClear={false}
        dataKey='id'
      />
      <DialogErrorMessageComponent errorMessages={errorMessages[rowIndex]} errorField={"internal"} />
    </>
  );
};