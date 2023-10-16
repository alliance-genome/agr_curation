import React from "react";
import { LiteratureAutocompleteTemplate } from "../../Autocomplete/LiteratureAutocompleteTemplate";
import { FormErrorMessageComponent } from "../../Error/FormErrorMessageComponent";
import { FormFieldWrapper } from "../../FormFieldWrapper";
import { referenceSearch } from "./utils";
import { AutocompleteFormEditor } from "../../Autocomplete/AutocompleteFormEditor";

export const SingleReferenceFormEditor = ({
  reference,
  onReferenceValueChange,
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
        fieldName=""
        formField={
          <AutocompleteFormEditor
            search={referenceSearch}
            name="singleReference"
            fieldName='singleReference'
            initialValue={reference}
            onValueChangeHandler={onReferenceValueChange}
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