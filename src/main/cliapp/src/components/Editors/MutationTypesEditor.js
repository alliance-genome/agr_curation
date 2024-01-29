import { AutocompleteMultiEditor } from "../Autocomplete/AutocompleteMultiEditor";
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { SubjectAutocompleteTemplate } from "../Autocomplete/SubjectAutocompleteTemplate";
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";

const mutationTypeSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ["name", "curie"];
	const endpoint = "soterm";
	const filterName = "mutationTypeFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
export const MutationTypesEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={mutationTypeSearch}
				initialValue={props?.rowData?.mutationTypes}
				rowProps={props}
				fieldName='mutationTypes'
				subField='curie'
				valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
					<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[dataKey]}
				errorField={"mutationTypes"}
			/>
		</>
	);
};