import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { variantTypeSearchConfig } from './utils';

export const VariantTypeTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="variantType"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...variantTypeSearchConfig}
	/>
);
