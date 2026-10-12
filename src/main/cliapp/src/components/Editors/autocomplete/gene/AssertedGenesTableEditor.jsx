import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { assertedGenesSearchConfig } from './utils';

export const AssertedGenesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="assertedGenes"
		subField="primaryExternalId"
		{...assertedGenesSearchConfig}
	/>
);
