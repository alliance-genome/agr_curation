import { buildSearchFn } from '../base/buildSearchFn';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const variantTypeSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'variantTypeFilter',
};

export const variantTypeSearch = buildSearchFn(variantTypeSearchConfig);
