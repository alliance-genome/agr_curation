import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { sourceGeneralConsequenceSearchConfig } from './utils';

export const SourceGeneralConsequenceTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="sourceGeneralConsequence"
		{...sourceGeneralConsequenceSearchConfig}
	/>
);
