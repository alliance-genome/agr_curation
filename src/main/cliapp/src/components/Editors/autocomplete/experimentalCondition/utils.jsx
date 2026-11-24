import { buildSearchFn } from '../base/buildSearchFn';
import { Endpoints } from '../../../../constants/Endpoints';
import { ExConAutocompleteTemplate } from '../base/templates/ExConAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const conditionsSearchConfig = {
	endpoint: Endpoints.Annotation.EXPERIMENTAL_CONDITION,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.experimentalConditionAutocompleteConfig),
	filterName: 'experimentalConditionFilter',
	valueDisplay: (item, setAutocompleteHoverItem, op, query) => (
		<ExConAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query} />
	),
};

// Searches every condition field set, so a chemical, anatomy or other term matches as well as the
// summary.
export const conditionsDetailedSearchConfig = {
	...conditionsSearchConfig,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.experimentalConditionDetailedAutocompleteConfig),
	filterName: 'conditionSummaryFilter',
};

export const conditionsSearch = buildSearchFn(conditionsSearchConfig);
