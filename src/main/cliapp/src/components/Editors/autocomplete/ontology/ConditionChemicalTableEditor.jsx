import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionChemicalSearchConfig } from './utils';

export const ConditionChemicalTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionChemical"
		{...conditionChemicalSearchConfig}
	/>
);
