import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierAllelesSearchConfig } from './utils';

export const DiseaseGeneticModifierAllelesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierAlleles"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...diseaseGeneticModifierAllelesSearchConfig}
	/>
);
