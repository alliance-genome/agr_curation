import { TableField } from '../../fields/TableField';
import { MultiAutocomplete } from '../../widgets/MultiAutocomplete';
import { SearchService } from '../../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../../../utils/utils';

/**
 * Multi-select autocomplete over the entities a search config describes, for a row's
 * `field`, with its validation message. Stores the selected entities, or an empty
 * array when none remain.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {string} [subField='curie'] - entity property used as the token label
 * @param {string} endpoint - search endpoint
 * @param {string[]} autocompleteFields - fields the query searches
 * @param {string} filterName - name of the filter group sent to the search
 * @param {object|(() => object)} [otherFilters] - extra filters, or a function returning
 *   them when the value is not ready until the search runs
 * @param {Function} [valueDisplay] - custom suggestion renderer
 * @returns {JSX.Element}
 */
export const AutocompleteMultiTableEditor = ({
	editorOptions,
	field,
	subField = 'curie',
	endpoint,
	autocompleteFields,
	filterName,
	otherFilters,
	valueDisplay,
}) => {
	const searchService = new SearchService();

	const search = (event, setFiltered, setQuery) => {
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const resolvedOtherFilters = typeof otherFilters === 'function' ? otherFilters() : otherFilters;
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, resolvedOtherFilters);
	};

	return (
		<TableField editorOptions={editorOptions} field={field}>
			{(binding) => <MultiAutocomplete {...binding} search={search} subField={subField} valueDisplay={valueDisplay} />}
		</TableField>
	);
};
