import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

const alleleValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const assertedAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'assertedAllelesFilter',
	valueDisplay: alleleValueDisplay,
};

export const diseaseGeneticModifierAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'geneticModifierAllelesFilter',
	valueDisplay: alleleValueDisplay,
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, config.otherFilters);
};

export const assertedAllelesSearch = buildSearchFn(assertedAllelesSearchConfig);
export const diseaseGeneticModifierAllelesSearch = buildSearchFn(diseaseGeneticModifierAllelesSearchConfig);
