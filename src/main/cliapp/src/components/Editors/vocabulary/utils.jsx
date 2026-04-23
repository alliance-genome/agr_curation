import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { VocabTermAutocompleteTemplate } from '../../Autocomplete/VocabTermAutocompleteTemplate';

export const vocabularySearchConfig = {
	endpoint: Endpoints.Vocabulary.VOCABULARY,
	autocompleteFields: ['name'],
	filterName: 'vocabularyFilter',
	valueDisplay: (item, setAutocompleteSelectedItem, op, query) => (
		<VocabTermAutocompleteTemplate
			item={item}
			setAutocompleteSelectedItem={setAutocompleteSelectedItem}
			op={op}
			query={query}
		/>
	),
};

export const vocabularySearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, vocabularySearchConfig.autocompleteFields);
	autocompleteSearch(
		searchService,
		vocabularySearchConfig.endpoint,
		vocabularySearchConfig.filterName,
		filter,
		setFiltered
	);
};
