import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { vocabularySearchConfig } from './utils';

export const VocabularyTableEditor = ({ editorOptions, field = 'vocabularyTermSetVocabulary' }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field={field}
		subField="name"
		{...vocabularySearchConfig}
	/>
);
