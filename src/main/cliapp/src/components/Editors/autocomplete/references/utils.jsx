import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { LiteratureAutocompleteTemplate } from '../base/templates/LiteratureAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

const referenceSearchConfig = {
	endpoint: Endpoints.Document.LITERATURE_REFERENCE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.referenceAutocompleteConfig),
	valueDisplay: (item, setAutocompleteHoverItem, op, query) => (
		<LiteratureAutocompleteTemplate
			item={item}
			setAutocompleteHoverItem={setAutocompleteHoverItem}
			op={op}
			query={query}
		/>
	),
};

export const singleReferenceSearchConfig = {
	...referenceSearchConfig,
	filterName: 'singleReferenceFilter',
};

export const multiReferenceSearchConfig = {
	...referenceSearchConfig,
	filterName: 'multiReferenceFilter',
};

export const multiReferenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const filter = buildAutocompleteFilter(event, multiReferenceSearchConfig.autocompleteFields);
	setInputValue(event.query);
	autocompleteSearch(
		searchService,
		multiReferenceSearchConfig.endpoint,
		multiReferenceSearchConfig.filterName,
		filter,
		setFiltered
	);
};

export const singleReferenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const filter = buildAutocompleteFilter(event, singleReferenceSearchConfig.autocompleteFields);
	setInputValue(event.query);
	autocompleteSearch(
		searchService,
		singleReferenceSearchConfig.endpoint,
		singleReferenceSearchConfig.filterName,
		filter,
		setFiltered
	);
};
