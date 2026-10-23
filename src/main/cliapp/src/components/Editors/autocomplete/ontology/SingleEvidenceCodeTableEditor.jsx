import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { evidenceCodesSearchConfig } from './utils';

/**
 * Autocomplete over ECO terms for a row's single `evidenceCode`, with its validation
 * message. Shares its search with the multi-valued evidence codes editor: the same
 * endpoint, fields and obsolete and subset filters.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @returns {JSX.Element}
 */
export const SingleEvidenceCodeTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="evidenceCode" {...evidenceCodesSearchConfig} />
);
