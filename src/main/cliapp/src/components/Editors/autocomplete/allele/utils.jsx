import { buildSearchFn } from '../base/buildSearchFn';
import { buildCuratorSpeciesFilter } from '../../../../utils/utils';
import { Endpoints } from '../../../../constants/Endpoints';
import { SubjectAutocompleteTemplate } from '../base/templates/SubjectAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

const alleleValueDisplay = (item, setAutocompleteHoverItem, op, query) => (
	<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
);

export const assertedAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'assertedAllelesFilter',
	otherFilters: () => buildCuratorSpeciesFilter(),
	valueDisplay: alleleValueDisplay,
};

export const diseaseGeneticModifierAllelesSearchConfig = {
	endpoint: Endpoints.Entity.ALLELE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.biologicalEntityAutocompleteConfig),
	filterName: 'geneticModifierAllelesFilter',
	otherFilters: () => buildCuratorSpeciesFilter(),
	valueDisplay: alleleValueDisplay,
};

export const assertedAllelesSearch = buildSearchFn(assertedAllelesSearchConfig);
export const diseaseGeneticModifierAllelesSearch = buildSearchFn(diseaseGeneticModifierAllelesSearchConfig);
