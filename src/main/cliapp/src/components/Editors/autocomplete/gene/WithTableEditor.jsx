import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { withSearchConfig } from './utils';

export const WithTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor
		editorOptions={editorOptions}
		field="with"
		subField="primaryExternalId"
		{...withSearchConfig}
	/>
);
