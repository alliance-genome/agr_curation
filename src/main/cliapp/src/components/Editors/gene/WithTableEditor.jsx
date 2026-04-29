import { AutocompleteMultiTableEditor } from '../autocomplete/base/AutocompleteMultiTableEditor';
import { withSearchConfig } from './utils';

export const WithTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="with"
		subField="primaryExternalId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...withSearchConfig}
	/>
);
