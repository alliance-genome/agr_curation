import { Dropdown } from 'primereact/dropdown';

// PrimeReact reads props.options unguarded when the panel opens, so a vocabulary
// that has not loaded yet must arrive as an empty list rather than undefined.
const EMPTY_OPTIONS = [];

/**
 * Single-select over a list of vocabulary terms. Emits the selected term object,
 * or the value of `optionValue` when that is set.
 *
 * @param {string} [id] - id for the focusable element, so a `<label htmlFor>` can point at
 *   it. Forwarded as PrimeReact's `inputId`.
 * @param {object|string|number|null} value - the currently selected term, or its
 *   `optionValue` property when that is set
 * @param {(value: object|string|number|null) => void} onChange - called with the selection,
 *   or null when cleared
 * @param {object[]} [options] - the terms to choose from
 * @param {string} [optionLabel='name'] - term property to display
 * @param {string} [optionValue] - term property to emit in place of the whole term
 * @param {string} [dataKey] - term property used to match `value` against `options` when they
 *   are not the same object instance. Ignored when `optionValue` is set, where matching
 *   compares the emitted values instead.
 * @param {boolean} [showClear=false] - whether to offer a clear affordance. PrimeReact
 *   renders it only once `options` is non-empty.
 * @param {string} [placeholder] - text shown when nothing is selected
 * @param {() => void} [onShow] - called when the panel opens, for loading options on demand
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function VocabularySelect({
	id,
	value,
	onChange,
	options,
	optionLabel = 'name',
	optionValue,
	dataKey,
	showClear = false,
	placeholder,
	onShow,
	name,
	invalid,
	disabled,
}) {
	return (
		<Dropdown
			inputId={id}
			aria-label={name}
			value={value ?? null}
			options={options || EMPTY_OPTIONS}
			optionLabel={optionLabel}
			optionValue={optionValue}
			dataKey={dataKey}
			onChange={(event) => onChange(event.value ?? null)}
			onShow={onShow}
			showClear={showClear}
			placeholder={placeholder}
			disabled={disabled}
			className={invalid ? 'p-invalid' : undefined}
			style={{ width: '100%' }}
		/>
	);
}
