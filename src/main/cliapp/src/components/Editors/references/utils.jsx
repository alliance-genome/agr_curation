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

// Single-reference pickers (DA.evidenceItem, ConditionRelation.singleReference)
// use singleReferenceFilter. Multi-reference (AllelesTable.references) keeps
// curieFilter above — the backend may treat the two filter names differently
// for multi-select.
export const singleReferenceSearchConfig = {
	...referenceSearchConfig,
	filterName: 'singleReferenceFilter',
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
