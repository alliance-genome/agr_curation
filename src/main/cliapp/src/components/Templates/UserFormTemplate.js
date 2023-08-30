import { FormFieldWrapper } from "../FormFieldWrapper";

export const UserFormTemplate = ({
  user,
  fieldName,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if (!user) user = <i>No data</i>;

  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName={fieldName}
        formField={user}
        additionalDataField={user}
      />
    </>
  );
};