import { buildAutocompleteFilter, autocompleteSearch } from '../../../../utils/utils';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';
import { VocabTermAutocompleteTemplate } from '../base/templates/VocabTermAutocompleteTemplate';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../../../constants/FilterFields';

export const memberTermsSearchConfig = {
	endpoint: Endpoints.Vocabulary.TERM,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.nameOnlyAutocompleteConfig),
	filterName: 'memberTermsFilter',
	valueDisplay: (item, setAutocompleteSelectedItem, op, query) => (
		<VocabTermAutocompleteTemplate
			item={item}
			setAutocompleteSelectedItem={setAutocompleteSelectedItem}
			op={op}
			query={query}
		/>
	),
};

// Helper builds the vocabulary-scoping filter from a plain vocabulary name.
// Callers supply otherFilters because the scoping value depends on the row's
// current state — keeping it out of the config keeps utils free of
// PrimeReact/editorOptions knowledge.
export const buildMemberTermsOtherFilters = (vocabularyName) => ({
	vocabularyFilter: {
		'vocabulary.name': { queryString: vocabularyName },
	},
});

export const memberTermsSearch = (event, setFiltered, setInputValue, otherFilters) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, memberTermsSearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		memberTermsSearchConfig.endpoint,
		memberTermsSearchConfig.filterName,
		filter,
		setFiltered,
		otherFilters
	);
};
