import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';

export const resourceDescriptorSearchConfig = {
	endpoint: Endpoints.Resource.DESCRIPTOR,
	autocompleteFields: ['prefix', 'name'],
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
