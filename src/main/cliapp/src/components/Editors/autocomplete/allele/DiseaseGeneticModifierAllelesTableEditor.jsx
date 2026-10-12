import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierAllelesSearchConfig } from './utils';

export const DiseaseGeneticModifierAllelesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierAlleles"
		subField="primaryExternalId"
		{...diseaseGeneticModifierAllelesSearchConfig}
	/>
);
