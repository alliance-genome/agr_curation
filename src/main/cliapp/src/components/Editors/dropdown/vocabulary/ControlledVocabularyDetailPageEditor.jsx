import { FormField } from '../../fields/FormField';
import { VocabularySelect } from '../../widgets/VocabularySelect';
import { useControlledVocabularyService } from '../../../../service/useControlledVocabularyService';

/**
 * A detail page dropdown over a controlled vocabulary. The selected value is the whole
 * vocabulary term, the shape the API reads back, matching what the table editors store.
 *
 * @param {object|null} value - the currently selected vocabulary term
 * @param {string} name - the field name, used for the dropdown and its error messages
 * @param {string} label - the field's display name
 * @param {string} vocabularyLabel - the vocabulary the options are loaded from
 * @param {(value: object|null) => void} onChange - called with the chosen term, or null when cleared
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field
 * @param {boolean} [showClear=true] - whether the value can be cleared
 * @param {boolean} [required=false] - marks the field as required
 * @returns {JSX.Element}
 */
export const ControlledVocabularyDetailPageEditor = ({
	value,
	name,
	label,
	vocabularyLabel,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	showClear = true,
	required = false,
}) => {
	const terms = useControlledVocabularyService(vocabularyLabel);

	return (
		<FormField
			label={label}
			name={name}
			value={value}
			onChange={onChange}
			errors={errorMessages}
			required={required}
			labelColumnSize={labelColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			additionalDataField={value?.name ? <div className="p-info">{value.name}</div> : null}
		>
			{(binding) => <VocabularySelect {...binding} options={terms} dataKey="id" showClear={showClear} />}
		</FormField>
	);
};
