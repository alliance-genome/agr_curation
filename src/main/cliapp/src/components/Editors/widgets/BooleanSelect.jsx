import { VocabularySelect } from './VocabularySelect';

const BOOLEAN_OPTIONS = [
	{ name: true, text: 'true' },
	{ name: false, text: 'false' },
];

/**
 * Select for a true/false field.
 *
 * @param {string} [id] - id for the focusable element, so a `<label htmlFor>` can point at it
 * @param {boolean|null} value - the current value
 * @param {(value: boolean|null) => void} onChange - called with a boolean, or null when cleared
 * @param {boolean} [showClear=false] - whether to offer a clear affordance
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function BooleanSelect({ id, value, onChange, showClear = false, name, invalid, disabled }) {
	return (
		<VocabularySelect
			id={id}
			value={value}
			onChange={onChange}
			options={BOOLEAN_OPTIONS}
			optionLabel="text"
			optionValue="name"
			showClear={showClear}
			name={name}
			invalid={invalid}
			disabled={disabled}
		/>
	);
}
