import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../../Autocomplete/SubjectAutocompleteTemplate';

const alleleValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const assertedAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	autocompleteFields: [
		'alleleSymbol.formatText',
		'alleleSymbol.displayText',
		'alleleFullName.formatText',
		'alleleFullName.displayText',
		'curie',
		'primaryExternalId',
		'modInternalId',
		'crossReferences.referencedCurie',
		'alleleSecondaryIds.secondaryId',
		'alleleSynonyms.formatText',
		'alleleSynonyms.displayText',
	],
	filterName: 'assertedAllelesFilter',
	valueDisplay: alleleValueDisplay,
};

export const diseaseGeneticModifierAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	// Modifier variant intentionally omits alleleSymbol.displayText (matches legacy behavior).
	autocompleteFields: [
		'alleleSymbol.formatText',
		'alleleFullName.formatText',
		'alleleFullName.displayText',
		'alleleSynonyms.formatText',
		'alleleSynonyms.displayText',
		'curie',
		'primaryExternalId',
		'modInternalId',
		'crossReferences.referencedCurie',
		'alleleSecondaryIds.secondaryId',
	],
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
