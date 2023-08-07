import React from "react"
import { AutocompleteFormMultiEditor } from '../../Autocomplete/AutocompleteFormMultiEditor';
import { LiteratureAutocompleteTemplate } from "../../Autocomplete/LiteratureAutocompleteTemplate";
import { FormErrorMessageComponent } from "../../Error/FormErrorMessageComponent";
import { ReferencesAdditionalFieldData } from "../../FieldData/ReferencesAdditionalFieldData";
import { referenceSearch } from "./utils";

export const ReferencesFormEditor = ({ 
  references, 
  onReferencesValueChange, 
  widgetColumnSize, 
  labelColumnSize, 
  fieldDetailsColumnSize, 
  errorMessages 
  }) => {

  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="referenes">References</label>
      </div>
      <div className={widgetColumnSize}>
        <AutocompleteFormMultiEditor
          search={referenceSearch}
          initialValue={references}
          fieldName='references'
          valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
            <LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
          onValueChangeHandler={onReferencesValueChange}
        />
      </div>
      <div className={fieldDetailsColumnSize}>
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"references"}/>
        <ReferencesAdditionalFieldData references={references}/>
      </div>
    </div>

  )
}