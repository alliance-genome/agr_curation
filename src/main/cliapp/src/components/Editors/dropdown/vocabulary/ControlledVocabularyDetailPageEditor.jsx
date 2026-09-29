import { Dropdown } from 'primereact/dropdown';
import { FormErrorMessageComponent } from '../../../Error/FormErrorMessageComponent';
import { DetailPageFieldWrapper } from '../../../DetailPageFieldWrapper';
import { useControlledVocabularyService } from '../../../../service/useControlledVocabularyService';

/**
 * A detail page dropdown over a controlled vocabulary. The selected value is the whole
 * vocabulary term, the shape the API reads back, matching what the table editors store.
 *
 * @param {Object} props
 * @param {Object} props.value - the currently selected vocabulary term
 * @param {string} props.name - the field name, used for the input and its error messages
 * @param {string} props.label - the field's display name
 * @param {string} props.vocabularyLabel - the vocabulary the options are loaded from
 * @param {Function} props.onValueChange
 */
export const ControlledVocabularyDetailPageEditor = ({
	value,
	name,
	label,
	vocabularyLabel,
	onValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	showClear = true,
	required = false,
}) => {
	const terms = useControlledVocabularyService(vocabularyLabel);

	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName={label}
			required={required}
			formField={
				<Dropdown
					name={name}
					value={value}
					options={terms || []}
					optionLabel="name"
					dataKey="id"
					onChange={onValueChange}
					showClear={showClear}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={name} />}
			additionalDataField={value?.name ? <div className="p-info">{value.name}</div> : null}
		/>
	);
};
