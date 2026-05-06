import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionIdSearchConfig } from './utils';

export const ConditionIdTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionId"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...conditionIdSearchConfig}
	/>
);
