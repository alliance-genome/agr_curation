import { DetailPageFieldWrapper } from '../DetailPageFieldWrapper';

export const DataProviderDetailPageTemplate = ({
	dataProvider,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
}) => {
	if (!dataProvider) return null;

	return (
		<>
			<DetailPageFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName="Data Provider"
				formField={dataProvider}
				additionalDataField={dataProvider}
			/>
		</>
	);
};
