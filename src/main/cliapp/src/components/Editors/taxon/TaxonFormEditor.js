import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { taxonSearch } from './utils';

export const TaxonFormEditor = ({ curie, onTaxonValueChange }) => {
  
  return (
    <div>
      <AutocompleteFormEditor
        search={taxonSearch}
        initialValue={curie}
        fieldName='taxon'
        onValueChangeHandler={onTaxonValueChange}
      />
    </div>
  )
}