import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const taxonSearchConfig = {
	endpoint: Endpoints.Ontology.NCBI_TAXON,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'taxonFilter',
};

export const taxonSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, taxonSearchConfig.autocompleteFields);
	autocompleteSearch(searchService, taxonSearchConfig.endpoint, taxonSearchConfig.filterName, filter, setFiltered);
};
