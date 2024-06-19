import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { LiteratureAutocompleteTemplate } from '../../Autocomplete/LiteratureAutocompleteTemplate';
import { referenceSearch } from './utils';
import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';

export const SingleReferenceTableEditor = ({ props, errorMessages, onChange }) => {
	return (
		<>
			<AutocompleteEditor
				search={referenceSearch}
				initialValue={props.rowData?.curie}
				rowProps={props}
				fieldName="references"
				valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
					<LiteratureAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setAutocompleteHoverItem}
						op={op}
						query={query}
					/>
				)}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[props?.rowIndex]} errorField={'select'} />
		</>
	);
};
