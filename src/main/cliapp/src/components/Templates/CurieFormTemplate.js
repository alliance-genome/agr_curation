import { FormFieldWrapper } from "../FormFieldWrapper";

export const CurieFormTemplate = ({
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