import { FormFieldWrapper } from "../FormFieldWrapper";

export const DataProviderFormTemplate = ({
  dataProvider,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {
  return (
    <FormFieldWrapper
      labelColumnSize={labelColumnSize}
      fieldDetailsColumnSize={fieldDetailsColumnSize}
      widgetColumnSize={widgetColumnSize}
      fieldName="Data Provider"
      formField={dataProvider}
      additionalDataField={dataProvider}
    />
  );
};