import { InputText } from 'primereact/inputtext';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { DetailPageFieldWrapper } from '../../DetailPageFieldWrapper';

/**
 * A single-line text field laid out as a detail page row.
 *
 * @param {Object} props
 * @param {string} [props.value]
 * @param {string} props.name - field name, used for the error lookup and aria label
 * @param {string} props.label
 * @param {({value: string, name: string}) => void} props.onValueChange - called with the new
 *   value, matching the other detail page editors rather than the native change event
 * @param {string} props.widgetColumnSize
 * @param {string} props.labelColumnSize
 * @param {string} props.fieldDetailsColumnSize
 * @param {Object} [props.errorMessages]
 * @param {boolean} [props.required]
 */
export const InputTextDetailPageEditor = ({
	value,
	name,
	label,
	onValueChange,
	widgetColumnSize,
	labelColumnSize,
	fieldDetailsColumnSize,
	errorMessages,
	required = false,
}) => {
	return (
		<DetailPageFieldWrapper
			labelColumnSize={labelColumnSize}
			fieldDetailsColumnSize={fieldDetailsColumnSize}
			widgetColumnSize={widgetColumnSize}
			fieldName={label}
			required={required}
			formField={
				<InputText
					aria-label={name}
					name={name}
					value={value ?? ''}
					onChange={(event) => onValueChange({ value: event.target.value, name })}
				/>
			}
			errorField={<FormErrorMessageComponent errorMessages={errorMessages} errorField={name} />}
		/>
	);
};
