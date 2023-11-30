import React from "react";
import { LiteratureAutocompleteTemplate } from "../../Autocomplete/LiteratureAutocompleteTemplate";
import { FormErrorMessageComponent } from "../../Error/FormErrorMessageComponent";
import { referenceSearch } from "./utils";
import { AutocompleteFormEditor } from "../../Autocomplete/AutocompleteFormEditor";

export const SingleReferenceFormEditor = ({
  reference,
  onReferenceValueChange,
  errorMessages,
  isLoading
}) => {
  return (
    <>
      <AutocompleteFormEditor
        inputClassNames="w-20rem"
        search={referenceSearch}
        name="singleReference"
        fieldName='singleReference'
        initialValue={reference}
        onValueChangeHandler={onReferenceValueChange}
        disabled={isLoading}
        valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
          <LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />}
      />
      <FormErrorMessageComponent errorMessages={errorMessages} errorField={"references"} />
    </>
  );
};