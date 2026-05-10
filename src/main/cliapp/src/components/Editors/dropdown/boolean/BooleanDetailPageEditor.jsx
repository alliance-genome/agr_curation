import { FormErrorMessageComponent } from '../../../Error/FormErrorMessageComponent';
import { Dropdown } from 'primereact/dropdown';
import { BooleanAdditionalFieldData } from '../../../FieldData/BooleanAdditionalFieldData';
import { DetailPageFieldWrapper } from '../../../DetailPageFieldWrapper';
import { useControlledVocabularyService } from '../../../../service/useControlledVocabularyService';

export const BooleanDetailPageEditor = ({
	value,
	name,
	label,
	onValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	showClear = false,
}) => {
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

	return (
		<>
			<DetailPageFieldWrapper
				labelColumnSize={labelColumnSize}
				fieldDetailsColumnSize={fieldDetailsColumnSize}
				widgetColumnSize={widgetColumnSize}
				fieldName={label}
				formField={
					<Dropdown
						name={name}
						value={value}
						options={booleanTerms?.terms || []}
						optionLabel="text"
						optionValue="name"
						onChange={onValueChange}
						showClear={showClear}
					/>
				}
				errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={name} />}
				additionalDataField={<BooleanAdditionalFieldData value={value?.toString()} />}
			/>
		</>
	);
};
