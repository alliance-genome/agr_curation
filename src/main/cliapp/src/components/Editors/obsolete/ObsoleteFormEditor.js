import React from 'react';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { Dropdown } from 'primereact/dropdown';
import { ObsoleteAdditionalFieldData } from '../../FieldData/ObsoleteAdditionalFieldData';

export const ObsoleteFormEditor = ({ 
    obsolete, 
    onObsoleteValueChange, 
    booleanTerms,
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="obsolete">Obsolete</label>
      </div>
      <div className={widgetColumnSize}>
        <Dropdown
          name="obsolete"
          value={obsolete}
          options={booleanTerms}
          optionLabel='text'
          optionValue='name'
          onChange={onObsoleteValueChange}
        />
      </div>
      <div className={fieldDetailsColumnSize}>
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"obsolete"}/>
        <ObsoleteAdditionalFieldData obsolete={obsolete.toString()}/>
      </div>
    </div>

  )
}