import React from "react";
import { TrueFalseDropdown } from "../../TrueFalseDropDownSelector";
import { ErrorMessageComponent } from "../../Error/ErrorMessageComponent";

const onIsExtinctEditorValueChange = (props, event) => {
  let updatedAlleles = [...props.props.value];

  if (event.value && event.value !== '') {
    updatedAlleles[props.rowIndex].isExtinct = JSON.parse(event.value.name);
  } else {
    updatedAlleles[props.rowIndex].isExtinct = null;
  }
};

export const IsExtinctTableEditor = ({ rowProps, errorMessagesRef, booleanTerms }) => {
  return (
    <>
      <TrueFalseDropdown
        options={booleanTerms}
        editorChange={onIsExtinctEditorValueChange}
        props={rowProps}
        field={"isExtinct"}
        showClear={true}
      />
      <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={"isExtinct"} />
    </>
  );
};