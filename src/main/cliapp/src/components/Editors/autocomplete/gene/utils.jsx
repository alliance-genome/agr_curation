import { buildAutocompleteFilter, autocompleteSearch, buildCuratorSpeciesFilter } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

const geneValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const withSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.geneAutocompleteConfig),
	filterName: 'withFilter',
	otherFilters: { taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:9606' } } },
	valueDisplay: geneValueDisplay,
};

export const assertedGenesSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.assertedGenesAutocompleteConfig),
	filterName: 'assertedGenesFilter',
	otherFilters: () => buildCuratorSpeciesFilter(),
	valueDisplay: geneValueDisplay,
};

export const diseaseGeneticModifierGenesSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.geneAutocompleteConfig),
	filterName: 'geneticModifierGenesFilter',
	otherFilters: () => buildCuratorSpeciesFilter(),
	valueDisplay: geneValueDisplay,
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	const otherFilters = typeof config.otherFilters === 'function' ? config.otherFilters() : config.otherFilters;
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, otherFilters);
};

export const withSearch = buildSearchFn(withSearchConfig);
export const assertedGenesSearch = buildSearchFn(assertedGenesSearchConfig);
export const diseaseGeneticModifierGenesSearch = buildSearchFn(diseaseGeneticModifierGenesSearchConfig);
