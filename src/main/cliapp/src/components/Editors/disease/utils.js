import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';

export const diseaseSearchConfig = {
	endpoint: Endpoints.Ontology.DO,
	autocompleteFields: ['curie', 'name', 'crossReferences.referencedCurie', 'secondaryIdentifiers', 'synonyms.name'],
	filterName: 'diseaseFilter',
	otherFilters: { obsoleteFilter: { obsolete: { queryString: false } } },
};

export const diseaseSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, diseaseSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		diseaseSearchConfig.endpoint,
		diseaseSearchConfig.filterName,
		filter,
		setFiltered,
		diseaseSearchConfig.otherFilters
	);
};
