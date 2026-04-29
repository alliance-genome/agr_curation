import { AutocompleteSingleTableEditor } from '../autocomplete/base/AutocompleteSingleTableEditor';
import { sourceGeneralConsequenceSearchConfig } from './utils';

export const SourceGeneralConsequenceTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="sourceGeneralConsequence"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...sourceGeneralConsequenceSearchConfig}
	/>
);
