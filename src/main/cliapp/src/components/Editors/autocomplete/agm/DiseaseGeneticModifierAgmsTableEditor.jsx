import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { diseaseGeneticModifierAgmsSearchConfig } from './utils';

export const DiseaseGeneticModifierAgmsTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="diseaseGeneticModifierAgms"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...diseaseGeneticModifierAgmsSearchConfig}
	/>
);
