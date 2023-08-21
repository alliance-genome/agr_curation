import { FormFieldWrapper } from "../FormFieldWrapper";

export const DateUpdatedFormTemplate = ({
  dateUpdated,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if(!dateUpdated) return null;

  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName="Date Updated"
      formField={dateUpdated}
      additionalDataField={dateUpdated}
    />
  );
};