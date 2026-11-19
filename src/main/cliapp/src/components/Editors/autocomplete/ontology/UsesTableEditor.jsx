import { TableField } from '../../fields/TableField';
import { MultiAutocomplete } from '../../widgets/MultiAutocomplete';
import { ontologyTermLabel, usesSearch } from './utils';

/**
 * Multi-select autocomplete over the experimental tool descriptor FBcv terms for a row's `uses`, with its
 * validation message. Each chosen term shows as its name and identifier.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @returns {JSX.Element}
 */
export const UsesTableEditor = ({ editorOptions }) => (
	<TableField editorOptions={editorOptions} field="uses">
		{(binding) => (
			<MultiAutocomplete {...binding} search={usesSearch} subField="curie" selectedItemTemplate={ontologyTermLabel} />
		)}
	</TableField>
);
