import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { conditionsSearchConfig } from './utils';

export const ConditionsTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="conditions"
		subField="conditionSummary"
		{...conditionsSearchConfig}
	/>
);
