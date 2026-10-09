import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { conditionGeneOntologySearchConfig } from './utils';

export const ConditionGeneOntologyTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionGeneOntology"
		{...conditionGeneOntologySearchConfig}
	/>
);
