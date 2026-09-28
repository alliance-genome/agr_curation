import { RequiredFieldMarker } from './RequiredFieldMarker';

export const DetailPageFieldWrapper = ({
	formField,
	errorField,
	additionalDataField,
	labelColumnSize,
	widgetColumnSize,
	fieldDetailsColumnSize,
	fieldName,
	showAdditionalData = true,
	required = false,
}) => {
	return (
		<div className="grid">
			<div className={labelColumnSize}>
				<h2 htmlFor={fieldName?.toLowerCase()}>
					{required && <RequiredFieldMarker />}
					{fieldName}
				</h2>
			</div>
			<div className={widgetColumnSize}>
				{formField}
				{errorField}
			</div>
			{showAdditionalData && <div className={fieldDetailsColumnSize}>{additionalDataField}</div>}
		</div>
	);
};
