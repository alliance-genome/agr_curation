import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { multiReferenceSearchConfig } from './utils';

export const ReferencesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor editorOptions={editorOptions} field="references" {...multiReferenceSearchConfig} />
);
