import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { taxonSearchConfig } from './utils';

export const TaxonTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="taxon"
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...taxonSearchConfig}
	/>
);
