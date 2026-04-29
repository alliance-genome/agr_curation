import { AutocompleteMultiTableEditor } from '../autocomplete/base/AutocompleteMultiTableEditor';
import { multiReferenceSearchConfig } from './utils';

export const ReferencesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="references"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...multiReferenceSearchConfig}
	/>
);
