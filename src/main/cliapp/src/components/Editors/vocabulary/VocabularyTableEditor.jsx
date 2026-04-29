import { AutocompleteSingleTableEditor } from '../autocomplete/base/AutocompleteSingleTableEditor';
import { vocabularySearchConfig } from './utils';

export const VocabularyTableEditor = ({
	editorOptions,
	field = 'vocabularyTermSetVocabulary',
	errorMessagesRef,
	uiErrorMessagesRef,
}) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field={field}
		subField="name"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...vocabularySearchConfig}
	/>
);
