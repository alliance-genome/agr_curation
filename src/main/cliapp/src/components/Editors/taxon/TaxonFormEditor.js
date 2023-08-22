import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { taxonSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { TaxonAdditionalFieldData } from '../../FieldData/TaxonAdditionalFieldData';
import { FormFieldWrapper } from '../../FormFieldWrapper';

export const TaxonFormEditor = ({
  taxon,
  onTaxonValueChange,
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
        fieldName="Taxon"
        formField={
          <AutocompleteFormEditor
            name="taxon-input"
            search={taxonSearch}
            initialValue={taxon}
            fieldName='taxon'
            onValueChangeHandler={onTaxonValueChange}
          />
        }
        errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={"taxon"} />}
        additionalDataField={<TaxonAdditionalFieldData curie={taxon?.curie} name={taxon?.name} />}
      />
    </>
  );
};
