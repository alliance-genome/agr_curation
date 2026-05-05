import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';

const baseGeneAutocompleteFields = [
	'geneSymbol.formatText',
	'geneSymbol.displayText',
	'geneFullName.formatText',
	'geneFullName.displayText',
	'geneSynonyms.formatText',
	'geneSynonyms.displayText',
	'geneSystematicName.formatText',
	'geneSystematicName.displayText',
	'geneSecondaryIds.secondaryId',
	'curie',
	'primaryExternalId',
	'modInternalId',
	'crossReferences.referencedCurie',
];

const geneValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const withSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	autocompleteFields: baseGeneAutocompleteFields,
	filterName: 'withFilter',
	otherFilters: { taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:9606' } } },
	valueDisplay: geneValueDisplay,
};

export const assertedGenesSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	// assertedGenes intentionally omits geneSecondaryIds.secondaryId (matches legacy filter behavior).
	autocompleteFields: [
		'geneSymbol.formatText',
		'geneSymbol.displayText',
		'geneFullName.formatText',
		'geneFullName.displayText',
		'curie',
		'primaryExternalId',
		'modInternalId',
		'crossReferences.referencedCurie',
		'geneSynonyms.formatText',
		'geneSynonyms.displayText',
		'geneSystematicName.formatText',
		'geneSystematicName.displayText',
	],
	filterName: 'assertedGenesFilter',
	valueDisplay: geneValueDisplay,
};

export const diseaseGeneticModifierGenesSearchConfig = {
	endpoint: Endpoints.Entity.GENE,
	autocompleteFields: baseGeneAutocompleteFields,
	filterName: 'geneticModifierGenesFilter',
	valueDisplay: geneValueDisplay,
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, config.otherFilters);
};

export const withSearch = buildSearchFn(withSearchConfig);
export const assertedGenesSearch = buildSearchFn(assertedGenesSearchConfig);
export const diseaseGeneticModifierGenesSearch = buildSearchFn(diseaseGeneticModifierGenesSearchConfig);
