import { FormField } from '../../fields/FormField';
import { BooleanSelect } from '../../widgets/BooleanSelect';
import { BooleanAdditionalFieldData } from '../../../FieldData/BooleanAdditionalFieldData';

/**
 * A detail page yes/no field.
 *
 * @param {boolean|null} value - the current value
 * @param {string} name - the field name, used for the dropdown and its error messages
 * @param {string} label - the field's display name
 * @param {(value: boolean|null) => void} onChange - called with the chosen value, or null when cleared
 * @param {string} [widgetColumnSize]
 * @param {string} [labelColumnSize]
 * @param {string} [fieldDetailsColumnSize]
 * @param {object} [errorMessages] - errors keyed by field
 * @param {boolean} [showClear=false] - whether the value can be cleared
 * @param {boolean} [isPending=false] - marks the field as having pending edits
 * @returns {JSX.Element}
 */
export const BooleanDetailPageEditor = ({
	value,
	name,
	label,
	onChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	showClear = false,
	isPending = false,
}) => (
	<FormField
		label={label}
		name={name}
		value={value}
		onChange={onChange}
		errors={errorMessages}
		isPending={isPending}
		labelColumnSize={labelColumnSize}
		widgetColumnSize={widgetColumnSize}
		fieldDetailsColumnSize={fieldDetailsColumnSize}
		additionalDataField={<BooleanAdditionalFieldData value={value?.toString()} />}
	>
		{(binding) => <BooleanSelect {...binding} showClear={showClear} />}
	</FormField>
);
