import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const geneTypeSearchConfig = {
	endpoint: Endpoints.Ontology.SO,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.ontologyTermAutocompleteConfig),
	filterName: 'geneTypeFilter',
};
