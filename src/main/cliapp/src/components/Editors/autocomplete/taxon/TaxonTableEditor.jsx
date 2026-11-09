import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { taxonSearchConfig } from './utils';

export const TaxonTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="taxon" {...taxonSearchConfig} />
);
