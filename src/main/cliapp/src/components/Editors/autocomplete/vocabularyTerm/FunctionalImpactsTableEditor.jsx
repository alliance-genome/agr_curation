import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { functionalImpactsSearchConfig } from './utils';

/**
 * Multi-select autocomplete over the allele_functional_impact vocabulary for a row's
 * `field`, with its validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} [field='functionalImpacts'] - the row property being edited
 * @returns {JSX.Element}
 */
export const FunctionalImpactsTableEditor = ({ editorOptions, field = 'functionalImpacts' }) => (
	<AutocompleteMultiTableEditor
		{...functionalImpactsSearchConfig}
		editorOptions={editorOptions}
		field={field}
		subField="name"
	/>
);
