import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';

export const sourceGeneralConsequenceSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: ['curie', 'name', 'secondaryIdentifiers', 'synonyms.name'],
	filterName: 'sourceGeneralConsequenceFilter',
};

export const sourceGeneralConsequenceSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, sourceGeneralConsequenceSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		sourceGeneralConsequenceSearchConfig.endpoint,
		sourceGeneralConsequenceSearchConfig.filterName,
		filter,
		setFiltered
	);
};
