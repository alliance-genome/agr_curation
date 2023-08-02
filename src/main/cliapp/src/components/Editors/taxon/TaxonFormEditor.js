import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { taxonSearch } from './utils';

export const TaxonFormEditor = ({ taxon, onTaxonValueChange }) => {
  
  return (
    <div>
      <AutocompleteFormEditor
        search={taxonSearch}
        initialValue={taxon}
        fieldName='taxon'
        onValueChangeHandler={onTaxonValueChange}
      />
    </div>
  )
}