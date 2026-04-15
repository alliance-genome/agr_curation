import React from 'react';
import { AutocompleteEditor } from '../../Autocomplete/AutocompleteEditor';
import { LiteratureAutocompleteTemplate } from '../../Autocomplete/LiteratureAutocompleteTemplate';
import { referenceSearch } from './utils';
import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';

export const SingleReferenceTableEditor = ({ editorOptions, errorMessages, onChange }) => {
	return (
		<>
			<AutocompleteEditor
				search={referenceSearch}
				initialValue={editorOptions.rowData?.curie}
				rowProps={editorOptions}
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
			<DialogErrorMessageComponent errorMessages={errorMessages[editorOptions?.rowIndex]} errorField={'select'} />
		</>
	);
};
