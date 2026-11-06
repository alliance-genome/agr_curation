import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { phenotypeTermSearchConfig } from './utils';

/**
 * Single-select autocomplete over phenotype terms for a row's `field`, with its validation
 * message.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} [field='phenotypeTerm'] - the row property being edited
 * @returns {JSX.Element}
 */
export const PhenotypeTermTableEditor = ({ editorOptions, field = 'phenotypeTerm' }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field={field} {...phenotypeTermSearchConfig} />
);
