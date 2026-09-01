import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { useSyncedState } from '../../../hooks/useSyncedState';

const toDisplay = (value) => (Array.isArray(value) ? value.join(', ') : '');

const toArray = (text) =>
	text
		? text
				.split(',')
				.map((entry) => entry.trim())
				.filter((entry) => entry.length > 0)
		: [];

/**
 * Comma-separated editor for a list of plain strings. Keeps the typed text as
 * its display state, so separators the array cannot represent (a trailing
 * comma, say) stay visible while the caller receives the parsed array.
 *
 * @param {string} [id] - DOM id, for label association
 * @param {string[]|null} value - the current entries
 * @param {(value: string[]) => void} onChange - called with trimmed, non-empty entries
 * @param {boolean} [multiline=false] - render a textarea instead of a single-line input
 * @param {number} [rows=5] - visible rows, when multiline
 * @param {string} [name] - input name and aria-label
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function StringListInput({ id, value, onChange, multiline = false, rows = 5, name, invalid, disabled }) {
	const [displayValue, setDisplayValue] = useSyncedState(toDisplay(value));

	const handleChange = (event) => {
		setDisplayValue(event.target.value);
		onChange(toArray(event.target.value));
	};

	const Control = multiline ? InputTextarea : InputText;

	return (
		<Control
			id={id}
			aria-label={name}
			name={name}
			value={displayValue}
			onChange={handleChange}
			disabled={disabled}
			className={invalid ? 'p-invalid' : undefined}
			style={{ width: '100%' }}
			{...(multiline ? { rows, autoResize: true } : {})}
		/>
	);
}
