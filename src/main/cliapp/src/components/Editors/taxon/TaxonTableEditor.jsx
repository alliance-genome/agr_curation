import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';
import { taxonSearch } from './utils';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const TaxonTableEditor = ({ editorOptions, errorMessagesRef }) => {
	const onTaxonValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'taxon', setFieldValue);
	};

	return (
		<>
			<AutocompleteEditor
				search={taxonSearch}
				initialValue={editorOptions.rowData.taxon?.curie}
				editorOptions={editorOptions}
				fieldName="taxon"
				onValueChangeHandler={onTaxonValueChange}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[editorOptions.rowIndex]} errorField="taxon" />
		</>
	);
};
