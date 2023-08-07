import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { inCollectionSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { InCollectionAdditionalFieldData } from '../../FieldData/InCollectionAdditionalFieldData';

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
        />
      </div>
      <div className={fieldDetailsColumnSize}>
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"inCollection"}/>
        <InCollectionAdditionalFieldData name={inCollection?.name}/>
      </div>
    </div>
  )
}