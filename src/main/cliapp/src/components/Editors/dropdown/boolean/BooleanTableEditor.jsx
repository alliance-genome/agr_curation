import { TableField } from '../../fields/TableField';
import { BooleanSelect } from '../../widgets/BooleanSelect';

/**
 * Boolean editor for a row's `field`, with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {boolean} [showClear=false] - whether to offer a clear affordance
 * @returns {JSX.Element}
 */
export const BooleanTableEditor = ({ editorOptions, field, showClear = false }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <BooleanSelect {...binding} showClear={showClear} />}
	</TableField>
);
