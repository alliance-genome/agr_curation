import { AutocompleteMultiEditor } from '../autocomplete/base/AutocompleteMultiEditor';
import { SearchService } from '../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, getIdentifier } from '../../../utils/utils';
import { DialogErrorMessageComponent } from '../../Error/DialogErrorMessageComponent';
import { Endpoints } from '../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../constants/FilterFields';

const mutationTypeSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig);
	const endpoint = Endpoints.Ontology.SO;
	const filterName = 'mutationTypeFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
// Matches the suggestion list, so a term reads the same before and after it is chosen.
const mutationTypeLabel = (term) => (term?.name ? `${term.name} (${getIdentifier(term)})` : getIdentifier(term));

export const MutationTypesEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={mutationTypeSearch}
				initialValue={props?.rowData?.mutationTypes}
				editorOptions={props}
				fieldName="mutationTypes"
				subField="curie"
				selectedItemTemplate={mutationTypeLabel}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'mutationTypes'} />
		</>
	);
};
