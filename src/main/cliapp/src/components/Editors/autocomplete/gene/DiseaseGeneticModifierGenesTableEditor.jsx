import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierGenesSearchConfig } from './utils';

export const DiseaseGeneticModifierGenesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierGenes"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...diseaseGeneticModifierGenesSearchConfig}
	/>
);
