import { DetailPageFieldWrapper } from '../DetailPageFieldWrapper';

export const IdentifierDetailPageTemplate = ({
	identifier,
	label,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	showAdditionalData,
}) => {
	return (
		<>
			<DetailPageFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName={label}
				formField={identifier}
				additionalDataField={identifier}
				showAdditionalData={showAdditionalData}
			/>
		</>
	);
};
