 import React from "react";
 import { AutocompleteEditor } from "../../Autocomplete/AutocompleteEditor";
 import { ErrorMessageComponent } from "../../Error/ErrorMessageComponent";
 import { variantTypeSearch } from "./utils";
 import { defaultAutocompleteOnChange } from "../../../utils/utils";

export const VariantTypeTableEditor = ({ rowProps, errorMessagesRef}) => {

  const onVariantTypeValueChange = (event, setFieldValue, props) => {
    defaultAutocompleteOnChange(props, event, "variantType", setFieldValue);
  };

  return (
    <>
      <AutocompleteEditor
        search={variantTypeSearch}
        initialValue={rowProps.rowData.variantType?.curie}
        rowProps={rowProps}
        fieldName='variantType'
        onValueChangeHandler={onVariantTypeValueChange}
      />
      <ErrorMessageComponent
        errorMessages={errorMessagesRef.current[rowProps.rowIndex]}
        errorField='variantType'
      />
    </>
  );
};