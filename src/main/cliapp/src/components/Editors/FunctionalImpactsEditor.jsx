import { AutocompleteMultiEditor } from '../Autocomplete/AutocompleteMultiEditor';
import { SearchService } from '../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../utils/utils';
import { VocabTermAutocompleteTemplate } from '../Autocomplete/VocabTermAutocompleteTemplate';
import { DialogErrorMessageComponent } from '../Error/DialogErrorMessageComponent';
import { Endpoints } from '../../constants/Endpoints';

const functionalImpactSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	const autocompleteFields = ['name'];
	const endpoint = Endpoints.Vocabulary.TERM;
	const filterName = 'functionalImpactFilter';
	const otherFilters = {
		vocabularyFilter: {
			'vocabulary.vocabularyLabel': {
				queryString: 'allele_functional_impact',
			},
		},
	};
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, autocompleteFields);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
};
export const FunctionalImpactsEditor = ({ props, errorMessages, onChange, dataKey }) => {
	return (
		<>
			<AutocompleteMultiEditor
				search={functionalImpactSearch}
				initialValue={props?.rowData?.functionalImpacts}
				editorOptions={props}
				fieldName="functionalImpacts"
				subField="name"
				valueDisplay={(item, setAutocompleteSelectedItem, op, query) => (
					<VocabTermAutocompleteTemplate
						item={item}
						setAutocompleteSelectedItem={setAutocompleteSelectedItem}
						op={op}
						query={query}
					/>
				)}
				onValueChangeHandler={onChange}
			/>
			<DialogErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField={'functionalImpacts'} />
		</>
	);
};
