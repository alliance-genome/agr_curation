import { AutocompleteMultiEditor } from "../Autocomplete/AutocompleteMultiEditor";
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";
import { LiteratureAutocompleteTemplate } from "../Autocomplete/LiteratureAutocompleteTemplate";

const referenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ["curie", "cross_references.curie"];
	const endpoint = "literature-reference";
	const filterName = "referencesFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
}

export const ReferencesEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={referenceSearch}
				initialValue={props?.rowData?.references}
				rowProps={props}
				fieldName='references'
				subField='curie'
				valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
					<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[dataKey]}
				errorField={"references"}
			/>
		</>
	);
};