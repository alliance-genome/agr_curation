import { AutocompleteSingleTableEditor } from '../autocomplete/AutocompleteSingleTableEditor';
import { getRefString } from '../../../utils/utils';
import { singleReferenceSearchConfig } from './utils';

export const SingleReferenceTableEditor = ({
	editorOptions,
	field = 'evidenceItem',
	errorMessagesRef,
	uiErrorMessagesRef,
}) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field={field}
		initialValue={getRefString(editorOptions.rowData[field])}
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...singleReferenceSearchConfig}
	/>
);
