export const FormFieldWrapper = ({
  formField,
  errorField,
  additionalDataField,
  labelColumnSize,
  widgetColumnSize,
  fieldDetailsColumnSize,
  fieldName
}) => {
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label htmlFor={fieldName?.toLowerCase()}>{fieldName}</label>
      </div>
      <div className={widgetColumnSize}>
        {formField}
        {errorField}
      </div>
      <div className={fieldDetailsColumnSize}>
        {additionalDataField}
      </div>
    </div>
  );

};