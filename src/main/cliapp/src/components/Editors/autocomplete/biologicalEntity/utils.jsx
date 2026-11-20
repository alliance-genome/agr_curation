import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { Endpoints } from '../../../../constants/Endpoints';
import { SearchService } from '../../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter, buildCuratorSpeciesFilter } from '../../../../utils/utils';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

// Biological entity disease annotations route to a per-type search endpoint
// based on the row's annotation type. The autocomplete field set is shared
// across all row types and lives in AUTOCOMPLETE_CONFIGS.

export const getBiologicalEntityEndpoint = (rowData) => {
	if (rowData?.type === 'GeneDiseaseAnnotation') return Endpoints.Entity.GENE;
	if (rowData?.type === 'AlleleDiseaseAnnotation') return Endpoints.Entity.ALLELE;
	if (rowData?.type === 'AGMDiseaseAnnotation') return Endpoints.Entity.AGM;
	return Endpoints.Entity.BIOLOGICAL_ENTITY;
};

export const biologicalEntityValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const diseaseAnnotationSubjectSearchConfig = {
	endpoint: Endpoints.Entity.BIOLOGICAL_ENTITY,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'diseaseAnnotationSubjectFilter',
	valueDisplay: biologicalEntityValueDisplay,
};

/**
 * Searches genes, alleles and AGMs of the curator's species for a disease annotation's subject.
 *
 * @param {{query: string}} event - the autocomplete's search event
 * @param {Function} setFiltered - receives the suggestions
 * @param {Function} setInputValue - receives the query
 */
export const diseaseAnnotationSubjectSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, diseaseAnnotationSubjectSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		diseaseAnnotationSubjectSearchConfig.endpoint,
		diseaseAnnotationSubjectSearchConfig.filterName,
		filter,
		setFiltered,
		buildCuratorSpeciesFilter()
	);
};
