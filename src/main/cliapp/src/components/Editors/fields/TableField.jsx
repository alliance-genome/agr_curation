import { useTableStrategy } from './TableEditorContext';
import { FieldErrors } from './FieldErrors';
import { normalizeError } from './normalizeError';

/**
 * Binds a widget to a table cell: resolves the value, routes changes to wherever
 * the active strategy stores them, and renders the field's validation messages.
 *
 * The widget receives `{value, row, rowKey, onChange, error, invalid, name}` and
 * stays unaware of which strategy is in play.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited. Under a strategy that stores
 *   through `editorCallback`, PrimeReact files the value under the Column's own
 *   `field`, so the two must name the same property — put any dotted display path on
 *   the Column's `columnKey` instead
 * @param {string} [errorField] - field to look errors up under, when it differs from
 *   the property being written; defaults to `field`
 * @param {(row: object) => any} [read] - derives the widget value from the row;
 *   defaults to `row[field]`
 * @param {(value: any, row: object) => any} [write] - derives the stored value from the
 *   widget value; defaults to the value unchanged
 * @param {'overlay'|'inline'|'text'} [errorLayout] - overrides the strategy's layout
 * @param {object} [strategy] - overrides the strategy from context
 * @param {((binding: object) => React.ReactNode)|React.ReactNode} children - the widget,
 *   as a render function; a plain node renders with only the error slot attached
 * @returns {JSX.Element}
 */
export function TableField({
	editorOptions,
	field,
	errorField = field,
	read,
	write,
	errorLayout,
	strategy: strategyProp,
	children,
}) {
	const contextStrategy = useTableStrategy();
	const strategy = strategyProp ?? contextStrategy;

	if (!strategy) {
		throw new Error(`TableField "${field}" was rendered outside a TableEditorProvider and given no strategy prop`);
	}

	const rowKey = strategy.keyOf(editorOptions);
	const row = strategy.readRow(editorOptions);

	const onChange = (next) => {
		const stored = write ? write(next, row) : next;
		strategy.write({ editorOptions, rowKey, field, value: stored });
	};

	const messages = strategy.errorsAt(rowKey, errorField).map((raw) => normalizeError(raw));
	const primaryError = messages.find(Boolean) ?? null;

	const binding = {
		value: read ? read(row) : row?.[field],
		row,
		rowKey,
		onChange,
		error: primaryError,
		invalid: primaryError !== null,
		name: errorField,
	};

	return (
		<>
			{typeof children === 'function' ? children(binding) : children}
			<FieldErrors messages={messages} layout={errorLayout ?? strategy.errorLayout} />
		</>
	);
}
