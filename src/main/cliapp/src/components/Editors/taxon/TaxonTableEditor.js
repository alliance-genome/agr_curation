import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { ErrorMessageComponent } from '../../Error/ErrorMessageComponent';
import { taxonSearch } from './utils';
import { defaultAutocompleteOnChange } from '../../../utils/utils';

export const TaxonTableEditor = ({ rowProps, errorMessagesRef }) => {
	const onTaxonValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, 'taxon', setFieldValue);
	};

	return (
		<>
			<AutocompleteEditor
				search={taxonSearch}
				initialValue={rowProps.rowData.taxon?.curie}
				rowProps={rowProps}
				fieldName="taxon"
				onValueChangeHandler={onTaxonValueChange}
			/>
			<ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField="taxon" />
		</>
	);
};
