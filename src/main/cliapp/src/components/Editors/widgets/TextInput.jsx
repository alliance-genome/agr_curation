import { InputText } from 'primereact/inputtext';
import { useSyncedState } from '../../../hooks/useSyncedState';

/**
 * Single-line text input. Holds its own display state so the caret survives a
 * re-render, and follows `value` when it is replaced externally.
 *
 * @param {string} [id] - DOM id, for label association
 * @param {string|null} value - the current text
 * @param {(value: string) => void} onChange - called with the raw string
 * @param {string} [name] - input name and aria-label
 * @param {boolean} [required]
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function TextInput({ id, value, onChange, name, required, invalid, disabled }) {
	const [displayValue, setDisplayValue] = useSyncedState(value ?? '');

	const handleChange = (event) => {
		setDisplayValue(event.target.value);
		onChange(event.target.value);
	};

	return (
		<InputText
			id={id}
			aria-label={name}
			name={name}
			required={required}
			value={displayValue}
			onChange={handleChange}
			disabled={disabled}
			className={invalid ? 'p-invalid' : undefined}
			style={{ width: '100%' }}
		/>
	);
}
