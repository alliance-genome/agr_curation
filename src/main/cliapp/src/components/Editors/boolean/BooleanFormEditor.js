import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { Dropdown } from 'primereact/dropdown';
import { BooleanAdditionalFieldData } from '../../FieldData/BooleanAdditionalFieldData';
import { FormFieldWrapper } from '../../FormFieldWrapper';
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
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName={label}
        formField={
          <Dropdown
            name={name}
            value={value}
            options={booleanTerms}
            optionLabel='text'
            optionValue='name'
            onChange={onValueChange}
          />
        }
        errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={name} />}
        additionalDataField={<BooleanAdditionalFieldData value={value?.toString()} />}
      />
    </>
  );
};