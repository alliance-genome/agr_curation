import { AutocompleteEditor } from '../Autocomplete/AutocompleteEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { Endpoints } from '../../constants/Endpoints';

const phenotypeTermSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ['name', 'curie'];
	const endpoint = Endpoints.Ontology.PHENOTYPE;
	const filterName = 'phenotypeTermFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
export const PhenotypeTermEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteEditor
				search={phenotypeTermSearch}
				initialValue={props.rowData.phenotypeTerm?.curie}
				editorOptions={props}
				fieldName="phenotypeTerm"
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'phenotypeTerm'} />
		</>
	);
};
