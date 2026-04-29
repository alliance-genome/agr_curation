import { AutocompleteSingleTableEditor } from '../autocomplete/base/AutocompleteSingleTableEditor';
import { conditionTaxonSearchConfig } from './utils';

export const ConditionTaxonTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="conditionTaxon"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...conditionTaxonSearchConfig}
	/>
);
