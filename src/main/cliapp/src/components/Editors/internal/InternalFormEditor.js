import React from 'react';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { Dropdown } from 'primereact/dropdown';
import { InternalAdditionalFieldData } from '../../FieldData/InternalAdditionalFieldData';

export const InternalFormEditor = ({ 
    internal, 
    onInternalValueChange, 
    booleanTerms,
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="internal">Internal</label>
      </div>
      <div className={widgetColumnSize}>
        <Dropdown
          name="internal"
          value={internal}
          options={booleanTerms}
          optionLabel='text'
          optionValue='name'
          onChange={onInternalValueChange}
        />
      </div>
      <div className={fieldDetailsColumnSize}>
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"internal"}/>
        <InternalAdditionalFieldData internal={internal.toString()}/>
      </div>
    </div>

  )
}