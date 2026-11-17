import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { getIdentifier } from '../../../../utils/utils';
import { alleleGeneObjectSearchConfig } from './utils';

/**
 * Autocomplete over genes for an allele-gene association's object, with its validation
 * message. A gene shows its primary external ID, or its next identifier when it has none.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @returns {JSX.Element}
 */
export const AlleleGeneObjectTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="alleleGeneAssociationObject"
		subField="primaryExternalId"
		toDisplay={(gene) => getIdentifier(gene) ?? ''}
		{...alleleGeneObjectSearchConfig}
	/>
);
