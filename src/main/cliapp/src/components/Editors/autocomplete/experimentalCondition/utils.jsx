import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
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

export const conditionsSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, conditionsSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		conditionsSearchConfig.endpoint,
		conditionsSearchConfig.filterName,
		filter,
		setFiltered
	);
};
