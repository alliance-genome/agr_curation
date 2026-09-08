import { TableField } from '../fields/TableField';
import { StringListInput } from '../widgets/StringListInput';

/**
 * Comma-separated editor for a row's list of plain strings, rendered as a textarea,
 * with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {number} [rows=5] - visible rows
 * @returns {JSX.Element}
 */
export const StringListTextAreaTableEditor = ({ editorOptions, field, rows = 5 }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <StringListInput {...binding} multiline rows={rows} />}
	</TableField>
);
