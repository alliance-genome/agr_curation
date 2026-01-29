export const FormFieldWrapper = ({
	formField,
	errorField,
	additionalDataField,
	labelColumnSize,
	widgetColumnSize,
	fieldDetailsColumnSize,
	fieldName,
	showAdditionalData = true,
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
			{showAdditionalData && <div className={fieldDetailsColumnSize}>{additionalDataField}</div>}
		</div>
	);
};
