import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';

export const variantTypeSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: ['curie', 'name', 'secondaryIdentifiers', 'synonyms.name'],
	filterName: 'variantTypeFilter',
};

export const variantTypeSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, variantTypeSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		variantTypeSearchConfig.endpoint,
		variantTypeSearchConfig.filterName,
		filter,
		setFiltered
	);
};
