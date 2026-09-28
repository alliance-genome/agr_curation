import { TableField } from '../fields/TableField';
import { TextInput } from '../widgets/TextInput';

/**
 * Text editor for a row's `field`, with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @returns {JSX.Element}
 */
export const InputTextTableEditor = ({ editorOptions, field }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <TextInput {...binding} />}
	</TableField>
);
