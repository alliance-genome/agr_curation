import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { conditionsSearchConfig } from './utils';

/**
 * Multi-select autocomplete over experimental conditions for a row's `conditions`, with its
 * validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {object} [searchConfig] - which condition field sets the query searches; defaults to the
 *   summary only, see conditionsDetailedSearchConfig for the rest
 * @returns {JSX.Element}
 */
export const ConditionsTableEditor = ({ editorOptions, searchConfig = conditionsSearchConfig }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="conditions"
		subField="conditionSummary"
		{...searchConfig}
	/>
);
