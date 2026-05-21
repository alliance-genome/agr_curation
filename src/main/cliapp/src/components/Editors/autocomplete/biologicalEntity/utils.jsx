import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { Endpoints } from '../../../../constants/Endpoints';

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
