import { useRef, useState } from 'react';
import { AutoComplete } from 'primereact/autocomplete';
import { EditorTooltip } from './EditorTooltip';
import { onSelectionOver, getIdentifier, getUniqueItemsByProperty } from '../../../utils/utils';
import { useSyncedState } from '../../../hooks/useSyncedState';

const EMPTY_ARRAY = [];

/**
 * Multi-select autocomplete over entities supplied by a caller-provided search.
 * Selections render as removable tokens and are de-duplicated by `id`. PrimeReact
 * skips only a suggestion deep-equal to a selected entity, so a search result that
 * shares an `id` with a selected entity but differs in other fields is still offered.
 *
 * `value` should be referentially stable between renders; a freshly built array
 * each render resets the token input.
 *
 * @param {string} [id] - id for the focusable element, so a `<label htmlFor>` can point at
 *   it. Forwarded as PrimeReact's `inputId`.
 * @param {object[]|null} value - the currently selected entities
 * @param {(value: object[]) => void} onChange - called with the de-duplicated entities,
 *   empty array when none remain
 * @param {(event: {query: string}, setSuggestions: Function, setQuery: Function) => void} search -
 *   populates suggestions; PrimeReact debounces this by 300ms
 * @param {string} [subField='curie'] - entity property used as the token label
 * @param {(item: object, setHoverItem: Function, tooltipRef: object, query: string) => JSX.Element} [valueDisplay] -
 *   custom suggestion renderer
 * @param {string} [name] - accessible name for the control
 * @param {boolean} [invalid] - applies invalid styling
 * @param {boolean} [disabled]
 * @returns {JSX.Element}
 */
export function MultiAutocomplete({
	id,
	value,
	onChange,
	search,
	subField = 'curie',
	valueDisplay,
	name,
	invalid,
	disabled,
}) {
	const [suggestions, setSuggestions] = useState([]);
	const [displayValue, setDisplayValue] = useSyncedState(value ?? EMPTY_ARRAY);
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

		if (!raw || raw.length === 0) {
			// The shared constant is used for display only; callers get a fresh array so
			// they never hold the module constant.
			setDisplayValue(EMPTY_ARRAY);
			onChange([]);
			return;
		}

		const deduplicated = getUniqueItemsByProperty(raw, 'id');
		setDisplayValue(deduplicated);
		onChange(deduplicated);
	};

	return (
		<div>
			<AutoComplete
				inputId={id}
				aria-label={name}
				multiple={true}
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
