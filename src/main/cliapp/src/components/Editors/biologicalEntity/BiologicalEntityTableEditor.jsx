import { AutocompleteSingleTableEditor } from '../autocomplete/base/AutocompleteSingleTableEditor';
import { getIdentifier } from '../../../utils/utils';
import {
	getBiologicalEntityEndpoint,
	getBiologicalEntityAutocompleteFields,
	biologicalEntityValueDisplay,
} from './utils';

export const BiologicalEntityTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="diseaseAnnotationSubject"
		subField="primaryExternalId"
		endpoint={getBiologicalEntityEndpoint(editorOptions.rowData)}
		autocompleteFields={getBiologicalEntityAutocompleteFields(editorOptions.rowData)}
		filterName="diseaseAnnotationSubjectFilter"
		initialValue={getIdentifier(editorOptions.rowData.diseaseAnnotationSubject)}
		valueDisplay={biologicalEntityValueDisplay}
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
	/>
);
