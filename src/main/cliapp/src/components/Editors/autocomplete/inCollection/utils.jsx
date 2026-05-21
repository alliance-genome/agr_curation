import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { VocabTermAutocompleteTemplate } from '../base/templates/VocabTermAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const inCollectionSearchConfig = {
	endpoint: Endpoints.Vocabulary.TERM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.nameOnlyAutocompleteConfig),
	filterName: 'inCollectionFilter',
	otherFilters: {
		vocabularyFilter: {
			'vocabulary.vocabularyLabel': {
				queryString: 'allele_collection',
			},
		},
	},
	valueDisplay: (item, setAutocompleteSelectedItem, op, query) => (
		<VocabTermAutocompleteTemplate
			item={item}
			op={op}
			query={query}
			setAutocompleteSelectedItem={setAutocompleteSelectedItem}
		/>
	),
};

export const inCollectionSearch = (event, setFiltered, setQuery) => {
	const searchService = new SearchService();
	setQuery(event.query);
	const filter = buildAutocompleteFilter(event, inCollectionSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		inCollectionSearchConfig.endpoint,
		inCollectionSearchConfig.filterName,
		filter,
		setFiltered,
		inCollectionSearchConfig.otherFilters
	);
};
