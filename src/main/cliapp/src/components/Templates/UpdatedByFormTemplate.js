import { FormFieldWrapper } from "../FormFieldWrapper";

export const UpdatedByFormTemplate = ({
  updatedBy,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if(!updatedBy) return null;

  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName="Updated By"
      formField={updatedBy}
      additionalDataField={updatedBy}
    />
  );
};