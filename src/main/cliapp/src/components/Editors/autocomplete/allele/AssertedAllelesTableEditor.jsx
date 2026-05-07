import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { assertedAllelesSearchConfig } from './utils';

export const AssertedAllelesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="assertedAlleles"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...assertedAllelesSearchConfig}
	/>
);
