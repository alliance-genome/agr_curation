import { buildSearchFn } from '../base/buildSearchFn';
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

export const inCollectionSearch = buildSearchFn(inCollectionSearchConfig);
