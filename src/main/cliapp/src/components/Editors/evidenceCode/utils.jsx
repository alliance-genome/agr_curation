import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { EvidenceAutocompleteTemplate } from '../../Autocomplete/EvidenceAutocompleteTemplate';

export const evidenceCodesSearchConfig = {
	endpoint: Endpoints.Ontology.ECO,
	autocompleteFields: ['curie', 'name', 'abbreviation'],
	filterName: 'evidenceFilter',
	otherFilters: {
		obsoleteFilter: { obsolete: { queryString: false } },
		subsetFilter: { subsets: { queryString: 'agr_eco_terms' } },
	},
	valueDisplay: (item, setAutocompleteHoverItem, op, query) => (
		<EvidenceAutocompleteTemplate
			item={item}
			setAutocompleteHoverItem={setAutocompleteHoverItem}
			op={op}
			query={query}
		/>
	),
};

export const evidenceCodesSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, evidenceCodesSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		evidenceCodesSearchConfig.endpoint,
		evidenceCodesSearchConfig.filterName,
		filter,
		setFiltered,
		evidenceCodesSearchConfig.otherFilters
	);
};
