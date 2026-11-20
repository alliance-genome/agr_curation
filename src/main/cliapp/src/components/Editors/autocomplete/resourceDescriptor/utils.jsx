import { buildSearchFn } from '../base/buildSearchFn';
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

export const resourceDescriptorSearch = buildSearchFn(resourceDescriptorSearchConfig);
