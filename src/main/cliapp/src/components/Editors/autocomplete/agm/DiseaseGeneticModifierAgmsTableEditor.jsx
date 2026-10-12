import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierAgmsSearchConfig } from './utils';

export const DiseaseGeneticModifierAgmsTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierAgms"
		subField="primaryExternalId"
		{...diseaseGeneticModifierAgmsSearchConfig}
	/>
);
