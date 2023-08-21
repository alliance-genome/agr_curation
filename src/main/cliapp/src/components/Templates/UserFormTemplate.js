import { FormFieldWrapper } from "../FormFieldWrapper";

export const UserFormTemplate = ({
  user,
  fieldName,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if(!user) return null;

  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName={fieldName}
      formField={user}
      additionalDataField={user}
    />
  );
};