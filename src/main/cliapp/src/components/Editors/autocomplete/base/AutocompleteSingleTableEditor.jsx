import { TableField } from '../../fields/TableField';
import { SingleAutocomplete } from '../../widgets/SingleAutocomplete';
import { SearchService } from '../../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../../../utils/utils';

/**
 * Autocomplete over the entities a search config describes, for a row's `field`,
 * with its validation message. Stores the selected entity, `{ [subField]: text }` for
 * free text, or null when emptied.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @param {string} field - the row property being edited
 * @param {string} [subField='curie'] - entity property used as the display and free-text key
 * @param {string} endpoint - search endpoint
 * @param {string[]} autocompleteFields - fields the query searches
 * @param {string} filterName - name of the filter group sent to the search
 * @param {object|(() => object)} [otherFilters] - extra filters, or a function returning
 *   them when the value is not ready until the search runs
 * @param {Function} [valueDisplay] - custom suggestion renderer
 * @param {string} [initialValue] - overrides the text shown for the current value
 * @returns {JSX.Element}
 */
export const AutocompleteSingleTableEditor = ({
	editorOptions,
	field,
	subField = 'curie',
	endpoint,
	autocompleteFields,
	filterName,
	otherFilters,
	valueDisplay,
	initialValue,
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
			{(binding) => (
				<SingleAutocomplete
					{...binding}
					search={search}
					subField={subField}
					toDisplay={initialValue === undefined ? undefined : () => initialValue}
					valueDisplay={valueDisplay}
				/>
			)}
		</TableField>
	);
};
