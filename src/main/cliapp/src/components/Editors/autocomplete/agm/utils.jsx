import { buildAutocompleteFilter, autocompleteSearch, buildCuratorSpeciesFilter } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

const agmValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const sgdStrainBackgroundSearchConfig = {
	endpoint: Endpoints.Entity.AGM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'sgdStrainBackgroundFilter',
	// SGD strain background is always S. cerevisiae S288C; match by taxon curie.
	otherFilters: { taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:559292', useKeywordFields: true } } },
	valueDisplay: agmValueDisplay,
};

export const diseaseGeneticModifierAgmsSearchConfig = {
	endpoint: Endpoints.Entity.AGM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'geneticModifierAgmsFilter',
	otherFilters: () => buildCuratorSpeciesFilter(),
	valueDisplay: agmValueDisplay,
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	const otherFilters = typeof config.otherFilters === 'function' ? config.otherFilters() : config.otherFilters;
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, otherFilters);
};

export const sgdStrainBackgroundSearch = buildSearchFn(sgdStrainBackgroundSearchConfig);
export const diseaseGeneticModifierAgmsSearch = buildSearchFn(diseaseGeneticModifierAgmsSearchConfig);
