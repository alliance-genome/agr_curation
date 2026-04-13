import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';
import { sourceGeneralConsequenceSearch } from './utils';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const SourceGeneralConsequenceTableEditor = ({ rowProps, errorMessagesRef }) => {
	const onSourceGeneralConsequenceValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'sourceGeneralConsequence', setFieldValue);
	};

	return (
		<>
			<AutocompleteEditor
				search={sourceGeneralConsequenceSearch}
				initialValue={rowProps.rowData.sourceGeneralConsequence?.curie}
				rowProps={rowProps}
				fieldName="sourceGeneralConsequence"
				onValueChangeHandler={onSourceGeneralConsequenceValueChange}
			/>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[rowProps.rowIndex]}
				errorField="sourceGeneralConsequence"
			/>
		</>
	);
};
