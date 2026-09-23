import { TableField } from '../../fields/TableField';
import { VocabularySelect } from '../../widgets/VocabularySelect';

/**
 * Dropdown over a supplied list of vocabulary terms for a row's `field`, with its
 * validation message. Stores the whole selected term.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {object[]} options - the terms to choose from
 * @param {boolean} [showClear=false] - whether to offer a clear affordance
 * @param {string} [dataKey] - term property used to match the row's value against
 *   `options` when they are not the same object instance
 * @param {string} [placeholderField='name'] - property of the row's value to show when the
 *   dropdown cannot match it against `options`, as when a vocabulary has not loaded
 * @returns {JSX.Element}
 */
export const ControlledVocabularyTableEditor = ({
	editorOptions,
	field,
	options,
	showClear = false,
	dataKey,
	placeholderField = 'name',
}) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => (
			<VocabularySelect
				{...binding}
				options={options}
				dataKey={dataKey}
				showClear={showClear}
				placeholder={binding.value?.[placeholderField]}
			/>
		)}
	</TableField>
);
