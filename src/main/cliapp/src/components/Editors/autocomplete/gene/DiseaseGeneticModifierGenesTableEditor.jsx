import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierGenesSearchConfig } from './utils';

export const DiseaseGeneticModifierGenesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierGenes"
		subField="primaryExternalId"
		{...diseaseGeneticModifierGenesSearchConfig}
	/>
);
