import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';

export const variantTypeSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	const autocompleteFields = ['curie', 'name', 'secondaryIdentifiers', 'synonyms.name'];
	const endpoint = Endpoints.Ontology.SO;
	const filterName = 'sourceGeneralConsequenceFilter';
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, autocompleteFields);
	autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
};
