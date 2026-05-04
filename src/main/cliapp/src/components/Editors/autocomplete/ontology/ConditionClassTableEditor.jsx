import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionClassSearchConfig } from './utils';

export const ConditionClassTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionClass"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...conditionClassSearchConfig}
	/>
);
