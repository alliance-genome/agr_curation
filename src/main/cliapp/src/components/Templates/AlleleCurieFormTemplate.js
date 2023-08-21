import { FormFieldWrapper } from "../FormFieldWrapper";

export const AlleleCurieFormTemplate = ({
  curie,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {
  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName="Curie"
      formField={curie}
      additionalDataField={curie}
    />
  );
};