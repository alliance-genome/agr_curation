import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const geneTypeSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'geneTypeFilter',
};

export const geneTypeSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, geneTypeSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		geneTypeSearchConfig.endpoint,
		geneTypeSearchConfig.filterName,
		filter,
		setFiltered
	);
};
