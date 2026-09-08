import { TableField } from '../fields/TableField';
import { StringListInput } from '../widgets/StringListInput';

/**
 * Comma-separated editor for a row's list of plain strings, with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @returns {JSX.Element}
 */
export const StringListTableEditor = ({ editorOptions, field }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <StringListInput {...binding} />}
	</TableField>
);
