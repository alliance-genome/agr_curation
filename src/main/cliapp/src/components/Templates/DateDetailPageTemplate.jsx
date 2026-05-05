import { DetailPageFieldWrapper } from '../DetailPageFieldWrapper';

export const DateDetailPageTemplate = ({
	date,
	fieldName,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
}) => {
	if (!date) date = <i>No data</i>;

	return (
		<>
			<DetailPageFieldWrapper
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
