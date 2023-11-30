import React from 'react';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';
import { inCollectionSearch } from './utils';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { InCollectionAdditionalFieldData } from '../../FieldData/InCollectionAdditionalFieldData';
import { VocabTermAutocompleteTemplate } from '../../Autocomplete/VocabTermAutocompleteTemplate';
import { FormFieldWrapper } from '../../FormFieldWrapper';

export const InCollectionFormEditor = ({
  inCollection,
  onInCollectionValueChange,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
  errorMessages,
  isLoading
}) => {

  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName="In Collection"
        formField={
          <AutocompleteFormEditor
            name="inCollection-input"
            search={inCollectionSearch}
            initialValue={inCollection}
            fieldName='inCollection'
            subField='name'
            onValueChangeHandler={onInCollectionValueChange}
            disabled={isLoading}
            valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
              <VocabTermAutocompleteTemplate item={item} op={op} query={query} setAutocompleteSelectedItem={setAutocompleteSelectedItem} />}
          />
        }
        errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={"inCollection"} />}
        additionalDataField={<InCollectionAdditionalFieldData name={inCollection?.name}/>}
      />
    </>
  );
};