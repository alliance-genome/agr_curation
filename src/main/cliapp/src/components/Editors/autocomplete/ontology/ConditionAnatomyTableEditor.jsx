import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionAnatomySearchConfig } from './utils';

export const ConditionAnatomyTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionAnatomy"
		{...conditionAnatomySearchConfig}
	/>
);
