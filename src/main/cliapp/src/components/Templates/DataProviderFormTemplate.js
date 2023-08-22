import { Divider } from "primereact/divider";
import { FormFieldWrapper } from "../FormFieldWrapper";

export const DataProviderFormTemplate = ({
  dataProvider,
  widgetColumnSize,
  labelColumnSize,
  fieldDetailsColumnSize,
}) => {

  if (!dataProvider) return null;

  return (
    <>
      <FormFieldWrapper
        labelColumnSize={labelColumnSize}
        fieldDetailsColumnSize={fieldDetailsColumnSize}
        widgetColumnSize={widgetColumnSize}
        fieldName="Data Provider"
        formField={dataProvider}
        additionalDataField={dataProvider}
      />
      <Divider/>
    </>
  );
};