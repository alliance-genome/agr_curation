import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const resourceDescriptorSearchConfig = {
	endpoint: Endpoints.Resource.DESCRIPTOR,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.resourceDescriptorAutocompleteConfig),
	filterName: 'resourceDescriptorFilter',
	valueDisplay: (item) => (
		<div>
			{item.prefix} ({item.name})
		</div>
	),
};

export const resourceDescriptorSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, resourceDescriptorSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		resourceDescriptorSearchConfig.endpoint,
		resourceDescriptorSearchConfig.filterName,
		filter,
		setFiltered
	);
};
