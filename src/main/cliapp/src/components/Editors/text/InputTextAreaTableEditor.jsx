import { TableField } from '../fields/TableField';
import { TextArea } from '../widgets/TextArea';

/**
 * Multi-line text editor for a row's `field`, with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {number} [rows] - visible rows
 * @param {number} [cols] - visible columns
 * @returns {JSX.Element}
 */
export const InputTextAreaTableEditor = ({ editorOptions, field, rows, cols }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <TextArea {...binding} rows={rows} cols={cols} />}
	</TableField>
);
