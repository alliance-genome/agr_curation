import { InputTextarea } from 'primereact/inputtextarea';
import { useSyncedState } from '../../../hooks/useSyncedState';

/**
 * Multi-line text input. Holds its own display state so the caret survives a
 * re-render, and follows `value` when it is replaced externally.
 *
 * @param {string} [id] - DOM id, for label association
 * @param {string|null} value - the current text
 * @param {(value: string) => void} onChange - called with the raw string
 * @param {number} [rows=5] - visible rows
 * @param {number} [cols] - visible columns
 * @param {boolean} [autoResize=false] - grow the control to fit its content
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function TextArea({ id, value, onChange, rows = 5, cols, autoResize = false, name, invalid, disabled }) {
	const [displayValue, setDisplayValue] = useSyncedState(value ?? '');

	const handleChange = (event) => {
		setDisplayValue(event.target.value);
		onChange(event.target.value);
	};

	return (
		<InputTextarea
			id={id}
			aria-label={name}
			value={displayValue}
			onChange={handleChange}
			rows={rows}
			cols={cols}
			autoResize={autoResize}
			disabled={disabled}
			className={invalid ? 'p-invalid' : undefined}
			style={{ width: '100%' }}
		/>
	);
}
