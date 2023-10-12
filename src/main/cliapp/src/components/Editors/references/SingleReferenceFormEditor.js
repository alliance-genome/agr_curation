import React from "react";
import { AutocompleteFormEditor } from "../../Autocomplete/AutocompleteFormEditor";
import { LiteratureAutocompleteTemplate } from "../../Autocomplete/LiteratureAutocompleteTemplate";
import { FormErrorMessageComponent } from "../../Error/FormErrorMessageComponent";
import { FormFieldWrapper } from "../../FormFieldWrapper";
import { referenceSearch } from "./utils";

export const ReferencesFormEditor = ({
  onReferencesValueChange,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
  errorMessages
}) => {
  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName="References"
        formField={
          <AutocompleteFormEditor
            search={referenceSearch}
            name="singleReference"
            fieldName='singleReference'
            initialValue={null}
            onValueChangeHandler={onReferencesValueChange}
            valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
              <LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />}
          />
        }
        errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={"references"} />}
        additionalDataField={null}
      />
    </>
  );
};