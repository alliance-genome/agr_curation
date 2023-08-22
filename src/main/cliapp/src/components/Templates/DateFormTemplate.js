import { FormFieldWrapper } from "../FormFieldWrapper";

export const DateFormTemplate = ({
  date,
  fieldName,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if (!date) date = <i>No data</i>;

  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName={fieldName}
        formField={date}
        additionalDataField={date}
      />
    </>
  );
};