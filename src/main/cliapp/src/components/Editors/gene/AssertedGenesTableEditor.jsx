import { AutocompleteMultiTableEditor } from '../autocomplete/base/AutocompleteMultiTableEditor';
import { assertedGenesSearchConfig } from './utils';

export const AssertedGenesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="assertedGenes"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...assertedGenesSearchConfig}
	/>
);
