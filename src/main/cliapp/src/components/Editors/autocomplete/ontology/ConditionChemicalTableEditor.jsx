import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionChemicalSearchConfig } from './utils';

export const ConditionChemicalTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionChemical"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...conditionChemicalSearchConfig}
	/>
);
