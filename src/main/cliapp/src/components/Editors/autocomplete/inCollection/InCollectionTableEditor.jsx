import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { inCollectionSearchConfig } from './utils';

export const InCollectionTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="inCollection"
		subField="name"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...inCollectionSearchConfig}
	/>
);
