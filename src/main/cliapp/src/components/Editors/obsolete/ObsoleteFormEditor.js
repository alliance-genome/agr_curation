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
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"obsolete"}/>
      </div>
      <div className={fieldDetailsColumnSize}>
        <ObsoleteAdditionalFieldData obsolete={obsolete?.toString()}/>
      </div>
    </div>

  )
}