import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { taxonSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { TaxonAdditionalFieldData } from '../../FieldData/TaxonAdditionalFieldData';

export const TaxonFormEditor = ({ 
    taxon, 
    onTaxonValueChange, 
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="taxon">Taxon</label>
      </div>
      <div className={widgetColumnSize}>
        <AutocompleteFormEditor
          name="taxon-input"
          search={taxonSearch}
          initialValue={taxon}
          fieldName='taxon'
          onValueChangeHandler={onTaxonValueChange}
        />
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"taxon"}/>
      </div>
      <div className={fieldDetailsColumnSize}>
        <TaxonAdditionalFieldData curie={taxon?.curie}/>
      </div>
    </div>
  )
}