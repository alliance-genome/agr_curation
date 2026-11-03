import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { evidenceSearchConfig } from './utils';

/**
 * Multi-select autocomplete over literature references for a row's `evidence`, with its
 * validation message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @returns {JSX.Element}
 */
export const EvidenceTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor editorOptions={editorOptions} field="evidence" {...evidenceSearchConfig} />
);
