import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { EvidenceAutocompleteTemplate } from '../autocomplete/base/templates/EvidenceAutocompleteTemplate';

export const curieAutocompleteFields = [
	'curie',
	'name',
	'crossReferences.referencedCurie',
	'secondaryIdentifiers',
	'synonyms.name',
];

export const diseaseSearchConfig = {
	endpoint: Endpoints.Ontology.DO,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'diseaseFilter',
	otherFilters: { obsoleteFilter: { obsolete: { queryString: false } } },
};

export const evidenceCodesSearchConfig = {
	endpoint: Endpoints.Ontology.ECO,
	autocompleteFields: ['curie', 'name', 'abbreviation'],
	filterName: 'evidenceFilter',
	otherFilters: {
		obsoleteFilter: { obsolete: { queryString: false } },
		subsetFilter: { subsets: { queryString: 'agr_eco_terms' } },
	},
	valueDisplay: (item, setAutocompleteHoverItem, op, query) => (
		<EvidenceAutocompleteTemplate
			item={item}
			setAutocompleteHoverItem={setAutocompleteHoverItem}
			op={op}
			query={query}
		/>
	),
};

export const conditionClassSearchConfig = {
	endpoint: Endpoints.Ontology.ZECO,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionClassFilter',
	otherFilters: { subsetFilter: { subsets: { queryString: 'ZECO_0000267' } } },
};

export const conditionIdSearchConfig = {
	endpoint: Endpoints.Ontology.EXPERIMENTAL_CONDITION,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionIdFilter',
};

export const conditionGeneOntologySearchConfig = {
	endpoint: Endpoints.Ontology.GO,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionGeneOntologyFilter',
};

export const conditionChemicalSearchConfig = {
	endpoint: Endpoints.Ontology.CHEMICAL,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionChemicalFilter',
};

export const conditionAnatomySearchConfig = {
	endpoint: Endpoints.Ontology.ANATOMICAL,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionAnatomyFilter',
};

export const conditionTaxonSearchConfig = {
	endpoint: Endpoints.Ontology.NCBI_TAXON,
	autocompleteFields: curieAutocompleteFields,
	filterName: 'conditionTaxonFilter',
};

const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, config.otherFilters);
};

export const diseaseSearch = buildSearchFn(diseaseSearchConfig);
export const evidenceCodesSearch = buildSearchFn(evidenceCodesSearchConfig);
export const conditionClassSearch = buildSearchFn(conditionClassSearchConfig);
export const conditionIdSearch = buildSearchFn(conditionIdSearchConfig);
export const conditionGeneOntologySearch = buildSearchFn(conditionGeneOntologySearchConfig);
export const conditionChemicalSearch = buildSearchFn(conditionChemicalSearchConfig);
export const conditionAnatomySearch = buildSearchFn(conditionAnatomySearchConfig);
export const conditionTaxonSearch = buildSearchFn(conditionTaxonSearchConfig);
