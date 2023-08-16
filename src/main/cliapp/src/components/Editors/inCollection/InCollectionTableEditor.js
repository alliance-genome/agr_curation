import React from "react";
import { defaultAutocompleteOnChange } from "../../../utils/utils";
import { AutocompleteEditor } from "../../Autocomplete/AutocompleteEditor";
import { inCollectionSearch } from "./utils";
import { VocabTermAutocompleteTemplate } from "../../Autocomplete/VocabTermAutocompleteTemplate";
import { ErrorMessageComponent } from "../../Error/ErrorMessageComponent";

const onInCollectionValueChange = (event, setFieldValue, props) => {
  defaultAutocompleteOnChange(props, event, "inCollection", setFieldValue, "name");
};

export const InCollectionTableEditor = ({ rowProps, errorMessagesRef } ) => {
  return (
    <>
      <AutocompleteEditor
        search={inCollectionSearch}
        initialValue={rowProps.rowData.inCollection?.name}
        rowProps={rowProps}
        fieldName='inCollection'
        onValueChangeHandler={onInCollectionValueChange}
        valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
          <VocabTermAutocompleteTemplate item={item} op={op} query={query} setAutocompleteSelectedItem={setAutocompleteSelectedItem}/>}
      />
      <ErrorMessageComponent
        errorMessages={errorMessagesRef.current[rowProps.rowIndex]}
        errorField='inCollection'
      />
    </>
  );
};