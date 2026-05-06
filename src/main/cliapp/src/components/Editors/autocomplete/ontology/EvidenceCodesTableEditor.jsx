import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { evidenceCodesSearchConfig } from './utils';

export const EvidenceCodesTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="evidenceCodes"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...evidenceCodesSearchConfig}
	/>
);
