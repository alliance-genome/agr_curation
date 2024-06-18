import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';

export const inCollectionSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	const autocompleteFields = ['name'];
	const endpoint = 'vocabularyterm';
	const filterName = 'taxonFilter';
	const otherFilters = {
		vocabularyFilter: {
			'vocabulary.vocabularyLabel': {
				queryString: 'allele_collection',
			},
		},
	};
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, autocompleteFields);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
};
