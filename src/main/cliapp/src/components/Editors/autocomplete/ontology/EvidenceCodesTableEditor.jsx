import { AutocompleteMultiTableEditor } from '../base/AutocompleteMultiTableEditor';
import { evidenceCodesSearchConfig } from './utils';

export const EvidenceCodesTableEditor = ({ editorOptions }) => (
	<AutocompleteMultiTableEditor editorOptions={editorOptions} field="evidenceCodes" {...evidenceCodesSearchConfig} />
);
