import { AutocompleteSingleTableEditor } from '../autocomplete/AutocompleteSingleTableEditor';
import { diseaseSearchConfig } from './utils';

export const DiseaseTableEditor = ({
	editorOptions,
	field = 'diseaseAnnotationObject',
	errorMessagesRef,
	uiErrorMessagesRef,
}) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field={field}
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
		{...diseaseSearchConfig}
	/>
);
