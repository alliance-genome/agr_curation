import React from 'react';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { IsExtinctAdditionalFieldData } from '../../FieldData/IsExtinctAdditionalFieldData';
import { Dropdown } from 'primereact/dropdown';

export const IsExtinctFormEditor = ({ 
    isExtinct, 
    onIsExtinctValueChange, 
    booleanTerms,
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor="isExtinct">Is Extinct</label>
      </div>
      <div className={widgetColumnSize}>
        <Dropdown
          name="isExtinct"
          value={isExtinct}
          options={booleanTerms}
          optionLabel='text'
          optionValue='name'
          onChange={onIsExtinctValueChange}
        />
      </div>
      <div className={fieldDetailsColumnSize}>
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={"isExtinct"}/>
        <IsExtinctAdditionalFieldData isExtinct={isExtinct.toString()}/>
      </div>
    </div>
  )
}