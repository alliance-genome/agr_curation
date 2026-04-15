import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';
import { sourceGeneralConsequenceSearch } from './utils';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const SourceGeneralConsequenceTableEditor = ({ editorOptions, errorMessagesRef }) => {
	const onSourceGeneralConsequenceValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'sourceGeneralConsequence', setFieldValue);
	};

	return (
		<>
			<AutocompleteEditor
				search={sourceGeneralConsequenceSearch}
				initialValue={editorOptions.rowData.sourceGeneralConsequence?.curie}
				editorOptions={editorOptions}
				fieldName="sourceGeneralConsequence"
				onValueChangeHandler={onSourceGeneralConsequenceValueChange}
			/>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField="sourceGeneralConsequence"
			/>
		</>
	);
};
