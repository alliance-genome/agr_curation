import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';
import { variantTypeSearch } from './utils';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const VariantTypeTableEditor = ({ editorOptions, errorMessagesRef }) => {
	const onVariantTypeValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'variantType', setFieldValue);
	};

	return (
		<>
			<AutocompleteEditor
				search={variantTypeSearch}
				initialValue={editorOptions.rowData.variantType?.curie}
				editorOptions={editorOptions}
				fieldName="variantType"
				onValueChangeHandler={onVariantTypeValueChange}
			/>
			<ErrorMessageComponent
				errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
				errorField="variantType"
			/>
		</>
	);
};
