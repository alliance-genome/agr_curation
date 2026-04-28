import { AutocompleteSingleTableEditor } from '../autocomplete/AutocompleteSingleTableEditor';
import { getIdentifier } from '../../../utils/utils';
import { sgdStrainBackgroundSearchConfig } from './utils';

export const SgdStrainBackgroundTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="sgdStrainBackground"
		subField="primaryExternalId"
		initialValue={getIdentifier(editorOptions.rowData.sgdStrainBackground)}
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...sgdStrainBackgroundSearchConfig}
	/>
);
