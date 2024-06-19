export const FormFieldWrapper = ({
	formField,
	errorField,
	additionalDataField,
	labelColumnSize,
	widgetColumnSize,
	fieldDetailsColumnSize,
	fieldName,
}) => {
	return (
		<div className="grid">
			<div className={labelColumnSize}>
				<h2 htmlFor={fieldName?.toLowerCase()}>{fieldName}</h2>
			</div>
			<div className={widgetColumnSize}>
				{formField}
				{errorField}
			</div>
			<div className={fieldDetailsColumnSize}>{additionalDataField}</div>
		</div>
	);
};
