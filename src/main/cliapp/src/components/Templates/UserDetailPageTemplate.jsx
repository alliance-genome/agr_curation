import { DetailPageFieldWrapper } from '../DetailPageFieldWrapper';

export const UserDetailPageTemplate = ({
	user,
	fieldName,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
}) => {
	if (!user) user = <i>No data</i>;

	return (
		<>
			<DetailPageFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName={fieldName}
				formField={user}
				additionalDataField={user}
			/>
		</>
	);
};
