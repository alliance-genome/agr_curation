import { AutocompleteMultiEditor } from "../Autocomplete/AutocompleteMultiEditor";
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { LiteratureAutocompleteTemplate } from '../Autocomplete/LiteratureAutocompleteTemplate';
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";

const evidenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ["curie", "cross_references.curie"];
	const endpoint = "literature-reference";
	const filterName = "evidenceFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
}
export const EvidenceEditor = ({ props, errorMessages, onChange }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={evidenceSearch}
				initialValue={props?.rowData?.evidence}
				rowProps={props}
				fieldName='evidence'
				subField='curie'
				valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
					<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[props?.rowIndex]}
				errorField={"evidence"}
			/>
		</>
	);
};