import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { assertedAllelesSearchConfig } from './utils';

export const AssertedAllelesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="assertedAlleles"
		subField="primaryExternalId"
		{...assertedAllelesSearchConfig}
	/>
);
