import { FormFieldWrapper } from "../FormFieldWrapper";

export const IdentifierFormTemplate = ({
  identifier,
  label,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {
  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName={label}
        formField={identifier}
        additionalDataField={identifier}
      />
    </>
  );
};