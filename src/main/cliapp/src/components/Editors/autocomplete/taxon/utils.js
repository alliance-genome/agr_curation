import { buildSearchFn } from '../base/buildSearchFn';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const taxonSearchConfig = {
	endpoint: Endpoints.Ontology.NCBI_TAXON,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'taxonFilter',
};

export const taxonSearch = buildSearchFn(taxonSearchConfig);
