import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { geneTypeSearchConfig } from './utils';

export const GeneTypeTableEditor = ({ editorOptions }) => (
	<AutocompleteSingleTableEditor editorOptions={editorOptions} field="geneType" {...geneTypeSearchConfig} />
);
