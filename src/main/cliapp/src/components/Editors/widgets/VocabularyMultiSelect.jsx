import { MultiSelect } from 'primereact/multiselect';

// With a value present, PrimeReact searches props.options unguarded when the panel
// opens, so a vocabulary that has not loaded yet must arrive as an empty list
// rather than undefined.
const EMPTY_OPTIONS = [];

/**
 * Multi-select over a list of vocabulary terms, displayed as chips.
 *
 * @param {string} [id] - id for the focusable element, so a `<label htmlFor>` can point at
 *   it. Forwarded as PrimeReact's `inputId`.
 * @param {object[]|null} value - the currently selected terms
 * @param {(value: object[]) => void} onChange - called with the selected terms, empty array
 *   when none remain
 * @param {object[]} [options] - the terms to choose from
 * @param {string} [optionLabel='name'] - term property to display
 * @param {string} [dataKey] - term property used to match `value` against `options` when they
 *   are not the same object instances
 * @param {string} [placeholder] - text shown when nothing is selected
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function VocabularyMultiSelect({
	id,
	value,
	onChange,
	options,
	optionLabel = 'name',
	dataKey,
	placeholder,
	name,
	invalid,
	disabled,
}) {
	return (
		<MultiSelect
			inputId={id}
			aria-label={name}
			value={value ?? []}
			options={options || EMPTY_OPTIONS}
			optionLabel={optionLabel}
			dataKey={dataKey}
			onChange={(event) => onChange(event.value)}
			display="chip"
			placeholder={placeholder}
			disabled={disabled}
			className={invalid ? 'p-invalid' : undefined}
			style={{ width: '100%' }}
		/>
	);
}
