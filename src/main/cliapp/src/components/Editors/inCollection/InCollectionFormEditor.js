import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { inCollectionSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { InCollectionAdditionalFieldData } from '../../FieldData/InCollectionAdditionalFieldData';
import { VocabTermAutocompleteTemplate } from '../../Autocomplete/VocabTermAutocompleteTemplate';

export const InCollectionFormEditor = ({ 
    inCollection, 
    onInCollectionValueChange, 
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="inCollection">In Collection</label>
      </div>
      <div className={widgetColumnSize}>
        <AutocompleteFormEditor
          name="inCollection-input"
          search={inCollectionSearch}
          initialValue={inCollection}
          fieldName='inCollection'
          subField='name'
          onValueChangeHandler={onInCollectionValueChange}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
						<VocabTermAutocompleteTemplate item={item} op={op} query={query} setAutocompleteSelectedItem={setAutocompleteSelectedItem}/>}
        />
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"inCollection"}/>
      </div>
      <div className={fieldDetailsColumnSize}>
        <InCollectionAdditionalFieldData name={inCollection?.name}/>
      </div>
    </div>
  )
}