import { AutocompleteSingleTableEditor } from '../base/AutocompleteSingleTableEditor';
import { getIdentifier } from '../../../../utils/utils';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';
import { getBiologicalEntityEndpoint, biologicalEntityValueDisplay } from './utils';

export const BiologicalEntityTableEditor = ({ editorOptions, errorMessagesRef, uiErrorMessagesRef }) => (
	<AutocompleteSingleTableEditor
		editorOptions={editorOptions}
		field="diseaseAnnotationSubject"
		subField="primaryExternalId"
		endpoint={getBiologicalEntityEndpoint(editorOptions.rowData)}
		autocompleteFields={getAutocompleteFields(AUTOCOMPLETE_CONFIGS.diseaseAnnotationSubjectAutocompleteConfig)}
		filterName="diseaseAnnotationSubjectFilter"
		initialValue={getIdentifier(editorOptions.rowData.diseaseAnnotationSubject)}
		valueDisplay={biologicalEntityValueDisplay}
		errorMessagesRef={errorMessagesRef}
		uiErrorMessagesRef={uiErrorMessagesRef}
	/>
);
