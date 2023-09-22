import { AutocompleteEditor } from '../Autocomplete/AutocompleteEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { DialogErrorMessageComponent } from "../Error/DialogErrorMessageComponent";

const phenotypeTermSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ["name", "curie"];
	const endpoint = "phenotypeterm";
	const filterName = "phenotypeTermFilter";
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
export const PhenotypeTermEditor = ({ props, errorMessages, onChange }) => {
	return (
		<>
			<AutocompleteEditor
				search={phenotypeTermSearch}
				initialValue={props.rowData.phenotypeTerm?.curie}
				rowProps={props}
				fieldName='phenotypeTerm'
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent
				errorMessages={errorMessages[props.rowIndex]}
				errorField={"phenotypeTerm"}
			/>
		</>
	);
};