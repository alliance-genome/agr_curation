import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';

export const taxonSearchConfig = {
	endpoint: Endpoints.Ontology.NCBI_TAXON,
	autocompleteFields: ['curie', 'name', 'crossReferences.referencedCurie', 'secondaryIdentifiers', 'synonyms.name'],
	filterName: 'taxonFilter',
};

export const taxonSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, taxonSearchConfig.autocompleteFields);
	autocompleteSearch(searchService, taxonSearchConfig.endpoint, taxonSearchConfig.filterName, filter, setFiltered);
};
