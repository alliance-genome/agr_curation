import { buildAutocompleteFilter, autocompleteSearch } from '../../../utils/utils';
import { SearchService } from '../../../service/SearchService';
import { Endpoints } from '../../../constants/Endpoints';
import { LiteratureAutocompleteTemplate } from '../../Autocomplete/LiteratureAutocompleteTemplate';

export const referenceSearchConfig = {
	endpoint: Endpoints.Document.LITERATURE_REFERENCE,
	autocompleteFields: ['curie', 'cross_references.curie'],
	filterName: 'curieFilter',
	valueDisplay: (item, setAutocompleteHoverItem, op, query) => (
		<LiteratureAutocompleteTemplate
			item={item}
			setAutocompleteHoverItem={setAutocompleteHoverItem}
			op={op}
			query={query}
		/>
	),
};

export const referenceSearch = (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	const filter = buildAutocompleteFilter(event, referenceSearchConfig.autocompleteFields);
	setInputValue(event.query);
	autocompleteSearch(
		searchService,
		referenceSearchConfig.endpoint,
		referenceSearchConfig.filterName,
		filter,
		setFiltered
	);
};
