import { FormFieldWrapper } from "../FormFieldWrapper";

export const DateFormTemplate = ({
  date,
  fieldName,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if(!date) return null;

  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName={fieldName}
      formField={date}
      additionalDataField={date}
    />
  );
};