import { AutocompleteEditor } from './autocomplete/base/AutocompleteEditor';
import { buildAutocompleteFilter, autocompleteSearch } from '../../utils/utils';
import { SearchService } from '../../service/SearchService';
import { EvidenceAutocompleteTemplate } from './autocomplete/base/templates/EvidenceAutocompleteTemplate';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';
import { Endpoints } from '../../constants/Endpoints';

const evidenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const autocompleteFields = ['curie', 'name', 'abbreviation'];
	const endpoint = Endpoints.Ontology.ECO;
	const filterName = 'evidenceFilter';
	const filter = buildAutocompleteFilter(event, autocompleteFields);
	const otherFilters = {
		obsoleteFilter: {
			obsolete: {
				queryString: false,
			},
		},
		subsetFilter: {
			subsets: {
				queryString: 'agr_eco_terms',
			},
		},
	};

	setInputValue(event.query);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
};

export const EvidenceCodeEditor = ({ props, errorMessages, onChangeHandler, dataKey }) => {
	return (
		<>
			<AutocompleteEditor
				search={evidenceSearch}
				initialValue={props.rowData.evidenceCode}
				editorOptions={props}
				fieldName="evidenceCode"
				valueDisplay={(item, setAutocompleteHoverItem, op, query) => (
					<EvidenceAutocompleteTemplate
						item={item}
						setAutocompleteHoverItem={setAutocompleteHoverItem}
						op={op}
						query={query}
					/>
				)}
				onValueChangeHandler={onChangeHandler}
			/>
			<ErrorMessageComponent errorMessages={errorMessages[dataKey]} errorField="evidenceCode" />
		</>
	);
};
