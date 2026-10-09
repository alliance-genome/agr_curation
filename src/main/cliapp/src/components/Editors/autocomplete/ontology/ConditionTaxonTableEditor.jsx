import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionTaxonSearchConfig } from './utils';

export const ConditionTaxonTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="conditionTaxon" {...conditionTaxonSearchConfig} />
);
