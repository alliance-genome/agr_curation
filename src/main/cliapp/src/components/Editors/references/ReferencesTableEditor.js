import React from "react";
import { AutocompleteMultiEditor } from "../../Autocomplete/AutocompleteMultiEditor";
import { LiteratureAutocompleteTemplate } from "../../Autocomplete/LiteratureAutocompleteTemplate";
import { ErrorMessageComponent } from "../../Error/ErrorMessageComponent";
import { referenceSearch } from "./utils";
import { multipleAutocompleteOnChange } from "../../../utils/utils";

const onReferenceValueChange = (event, setFieldValue, props) => {
  multipleAutocompleteOnChange(props, event, "references", setFieldValue);
};


export const ReferencesTableEditor = ({ rowProps, errorMessagesRef }) => {
  return (
    <>
      <AutocompleteMultiEditor
        search={referenceSearch}
        initialValue={rowProps.rowData.references}
        rowProps={rowProps}
        fieldName='references'
        valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
          <LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
        onValueChangeHandler={onReferenceValueChange}
      />
      <ErrorMessageComponent
        errorMessages={errorMessagesRef.current[rowProps.rowIndex]}
        errorField={"references"}
      />
    </>
  );
};