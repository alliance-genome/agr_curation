import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { Dropdown } from 'primereact/dropdown';
import { BooleanAdditionalFieldData } from '../../FieldData/BooleanAdditionalFieldData';
import { useControlledVocabularyService } from "../../../service/useControlledVocabularyService";

export const BooleanFormEditor = ({ 
    value, 
    name,
    label,
    onValueChange, 
    widgetColumnSize, 
    labelColumnSize, 
    fieldDetailsColumnSize, 
    errorMessages 
  }) => {

  const booleanTerms = useControlledVocabularyService("generic_boolean_terms");
  
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor={name}>{label}</label>
      </div>
      <div className={widgetColumnSize}>
        <Dropdown
          name={name}
          value={value}
          options={booleanTerms}
          optionLabel='text'
          optionValue='name'
          onChange={onValueChange}
        />
        <FormErrorMessageComponent errorMessages={errorMessages} errorField={name}/>
      </div>
      <div className={fieldDetailsColumnSize}>
        <BooleanAdditionalFieldData value={value?.toString()}/>
      </div>
    </div>

  )
}