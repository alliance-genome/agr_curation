import { AutocompleteMultiTableEditor } from '../autocomplete/base/AutocompleteMultiTableEditor';
import { conditionsSearchConfig } from './utils';

export const ConditionsTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="conditions"
		subField="conditionSummary"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...conditionsSearchConfig}
	/>
);
