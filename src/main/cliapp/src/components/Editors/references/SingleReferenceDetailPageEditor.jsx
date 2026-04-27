import React from 'react';
import { LiteratureAutocompleteTemplate } from '../../Autocomplete/LiteratureAutocompleteTemplate';
import { FormErrorMessageComponent } from '../../Error/FormErrorMessageComponent';
import { singleReferenceSearch } from './utils';
import { AutocompleteFormEditor } from '../../Autocomplete/AutocompleteFormEditor';

export const SingleReferenceDetailPageEditor = ({ reference, onReferenceValueChange, errorMessages }) => {
	return (
		<>
			<AutocompleteFormEditor
				inputClassNames="w-20rem"
				search={singleReferenceSearch}
				name="singleReference"
				fieldName="singleReference"
				initialValue={reference}
				onValueChangeHandler={onReferenceValueChange}
				valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
					<LiteratureAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setAutocompleteHoverItem}
						op={op}
						query={query}
					/>
				)}
			/>
			<FormErrorMessageComponent errorMessages={errorMessages} errorField={'references'} />
		</>
	);
};
