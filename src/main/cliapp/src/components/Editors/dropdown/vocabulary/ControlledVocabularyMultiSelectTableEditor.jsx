import { TableField } from '../../fields/TableField';
import { VocabularyMultiSelect } from '../../widgets/VocabularyMultiSelect';

/**
 * Multi-select over a supplied list of vocabulary terms for a row's `field`, with its
 * validation message. Stores the whole selected terms.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {object[]} options - the terms to choose from
 * @returns {JSX.Element}
 */
export const ControlledVocabularyMultiSelectTableEditor = ({ editorOptions, field, options }) => (
	<TableField editorOptions={editorOptions} field={field}>
		{(binding) => <VocabularyMultiSelect {...binding} options={options} />}
	</TableField>
);
