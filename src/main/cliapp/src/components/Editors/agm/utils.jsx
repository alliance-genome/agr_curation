import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../../Autocomplete/SubjectAutocompleteTemplate';

const agmValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const sgdStrainBackgroundSearchConfig = {
	endpoint: Endpoints.Entity.AGM,
	autocompleteFields: ['name', 'curie', 'primaryExternalId', 'modInternalId', 'crossReferences.referencedCurie'],
	filterName: 'sgdStrainBackgroundFilter',
	otherFilters: { taxonFilter: { 'taxon.name': { queryString: 'Saccharomyces cerevisiae' } } },
	valueDisplay: agmValueDisplay,
};

export const diseaseGeneticModifierAgmsSearchConfig = {
	endpoint: Endpoints.Entity.AGM,
	autocompleteFields: [
		'agmFullName.formatText',
		'agmSynonyms.formatText',
		'agmFullName.displayText',
		'agmSynonyms.displayText',
		'agmSecondaryIds.secondaryId',
		'curie',
		'primaryExternalId',
		'modInternalId',
		'crossReferences.referencedCurie',
	],
	filterName: 'geneticModifierAgmsFilter',
	valueDisplay: agmValueDisplay,
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, config.otherFilters);
};

export const sgdStrainBackgroundSearch = buildSearchFn(sgdStrainBackgroundSearchConfig);
export const diseaseGeneticModifierAgmsSearch = buildSearchFn(diseaseGeneticModifierAgmsSearchConfig);
