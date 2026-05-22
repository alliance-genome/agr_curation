import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { EvidenceAutocompleteTemplate } from '../base/templates/EvidenceAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const ontologyTermAutocompleteFields = getAutocompleteFields(
	AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig
);

export const diseaseSearchConfig = {
	endpoint: Endpoints.Ontology.DO,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'diseaseFilter',
	otherFilters: { obsoleteFilter: { obsolete: { queryString: false } } },
};

export const evidenceCodesSearchConfig = {
	endpoint: Endpoints.Ontology.ECO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.evidenceCodeAutocompleteConfig),
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
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'conditionClassFilter',
	otherFilters: { subsetFilter: { subsets: { queryString: 'ZECO_0000267' } } },
};

export const conditionIdSearchConfig = {
	endpoint: Endpoints.Ontology.EXPERIMENTAL_CONDITION,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'conditionIdFilter',
};

export const conditionGeneOntologySearchConfig = {
	endpoint: Endpoints.Ontology.GO,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'conditionGeneOntologyFilter',
};

export const conditionChemicalSearchConfig = {
	endpoint: Endpoints.Ontology.CHEMICAL,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'conditionChemicalFilter',
};

export const conditionAnatomySearchConfig = {
	endpoint: Endpoints.Ontology.ANATOMICAL,
	autocompleteFields: ontologyTermAutocompleteFields,
	filterName: 'conditionAnatomyFilter',
};

export const conditionTaxonSearchConfig = {
	endpoint: Endpoints.Ontology.NCBI_TAXON,
	autocompleteFields: ontologyTermAutocompleteFields,
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
