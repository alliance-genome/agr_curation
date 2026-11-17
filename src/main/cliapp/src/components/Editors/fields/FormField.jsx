import { DetailPageFieldWrapper } from '../../DetailPageFieldWrapper';
import { FieldErrors } from './FieldErrors';
import { normalizeError } from './normalizeError';

/**
 * Binds a widget to a labelled detail-page field, laid out by DetailPageFieldWrapper, and renders
 * the field's validation message beneath it.
 *
 * The widget receives `{value, onChange, error, invalid, name}`, the binding TableField supplies
 * without its row entries.
 *
 * @param {string} label - the field's heading
 * @param {string} name - the widget's accessible name, and the key its error is looked up under
 * @param {any} value - the current value
 * @param {(value: any) => void} onChange - called with the new value
 * @param {object} [errors] - errors keyed by field, each a string or `{severity, message}`
 * @param {string} [errorField] - the key to look the error up under; defaults to `name`
 * @param {((binding: object) => React.ReactNode)|React.ReactNode} children - the widget, as a
 *   render function, or a plain node that is rendered as is
 * @param {object} [wrapperProps] - the remaining props go to DetailPageFieldWrapper:
 *   `required`, `isPending`, `labelColumnSize`, `widgetColumnSize`, `fieldDetailsColumnSize`,
 *   `additionalDataField` and `showAdditionalData`
 * @returns {JSX.Element}
 */
export function FormField({ label, name, value, onChange, errors, errorField, children, ...wrapperProps }) {
	const error = normalizeError(errors?.[errorField ?? name]);

	const binding = {
		value,
		onChange,
		error,
		invalid: error !== null,
		name,
	};

	return (
		<DetailPageFieldWrapper
			fieldName={label}
			formField={typeof children === 'function' ? children(binding) : children}
			errorField={<FieldErrors messages={[error]} layout="text" />}
			{...wrapperProps}
		/>
	);
}
