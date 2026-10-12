import { useRef, useState } from 'react';
import { AutoComplete } from 'primereact/autocomplete';
import { EditorTooltip } from './EditorTooltip';
import { onSelectionOver, getIdentifier } from '../../../utils/utils';
import { useSyncedState } from '../../../hooks/useSyncedState';

/**
 * Single-select autocomplete over entities supplied by a caller-provided search.
 * Selecting a suggestion emits that entity; free text is emitted as
 * `{ [subField]: text }`.
 *
 * @param {string} [id] - id for the input, so a `<label htmlFor>` can point at it.
 *   Forwarded as PrimeReact's `inputId`.
 * @param {object|null} value - the currently selected entity
 * @param {(value: object|null) => void} onChange - called with the entity, or null when emptied
 * @param {(event: {query: string}, setSuggestions: Function, setQuery: Function) => void} search -
 *   populates suggestions; PrimeReact debounces this by 300ms
 * @param {string} [subField='curie'] - entity property used as the display and free-text key
 * @param {(value: object|null) => string} [toDisplay] - derives the input text from `value`,
 *   defaulting to `value[subField]`
 * @param {(item: object, setHoverItem: Function, tooltipRef: object, query: string) => JSX.Element} [valueDisplay] -
 *   custom suggestion renderer
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function SingleAutocomplete({
	id,
	value,
	onChange,
	search,
	subField = 'curie',
	toDisplay,
	valueDisplay,
	name,
	invalid,
	disabled,
}) {
	const [suggestions, setSuggestions] = useState([]);
	const derive = toDisplay ?? ((modelValue) => modelValue?.[subField] ?? '');
	const [displayValue, setDisplayValue] = useSyncedState(derive(value));
	const [query, setQuery] = useState('');
	const [hoverItem, setHoverItem] = useState({});
	const tooltip = useRef(null);

	const itemTemplate = (item) => {
		if (valueDisplay) return valueDisplay(item, setHoverItem, tooltip, query);

		return (
			<div>
				<div
					onMouseOver={(event) => onSelectionOver(event, item, query, tooltip, setHoverItem)}
					dangerouslySetInnerHTML={{ __html: item.name + ' (' + getIdentifier(item) + ') ' }}
				/>
			</div>
		);
	};

	const handleChange = (event) => {
		const raw = event.value;

		if (!raw) {
			setDisplayValue('');
			onChange(null);
			return;
		}
		if (typeof raw === 'object') {
			setDisplayValue(raw[subField] ?? '');
			onChange(raw);
			return;
		}
		setDisplayValue(raw);
		onChange({ [subField]: raw });
	};

	return (
		<div>
			<AutoComplete
				inputId={id}
				aria-label={name}
				field={subField}
				value={displayValue}
				suggestions={suggestions}
				itemTemplate={itemTemplate}
				completeMethod={(event) => search(event, setSuggestions, setQuery)}
				onHide={(event) => tooltip.current?.hide(event)}
				onChange={handleChange}
				disabled={disabled}
				className={invalid ? 'p-invalid' : undefined}
				panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px' }}
			/>
			<EditorTooltip op={tooltip} autocompleteHoverItem={hoverItem} />
		</div>
	);
}
