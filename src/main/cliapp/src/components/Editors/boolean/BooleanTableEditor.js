import React from "react";
import { TrueFalseDropdown } from "../../TrueFalseDropDownSelector";
import { ErrorMessageComponent } from "../../Error/ErrorMessageComponent";
import { useControlledVocabularyService } from "../../../service/useControlledVocabularyService";

export const BooleanTableEditor = ({ rowProps, errorMessagesRef, field}) => {

  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");

  const editorChange = (props, event) => {
    let updatedEntities = [...props.props.value];

    if (event.value && event.value !== '') {
      updatedEntities[props.rowIndex][field]= JSON.parse(event.value.name);
    } else {
      updatedEntities[props.rowIndex][field] = null;
    }
  };

  return (
    <>
      <TrueFalseDropdown
        options={booleanTerms}
        editorChange={editorChange}
        props={rowProps}
        field={field}
        showClear={true}
      />
      <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={field} />
    </>
  );
};