import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const sourceGeneralConsequenceSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
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
