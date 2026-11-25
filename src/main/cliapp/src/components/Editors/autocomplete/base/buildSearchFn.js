import { SearchService } from '../../../../service/SearchService';
import { autocompleteSearch, buildAutocompleteFilter } from '../../../../utils/utils';

/**
 * Builds an autocomplete search over the entities a search config describes.
 *
 * @param {object} config - the search config
 * @param {string} config.endpoint - search endpoint
 * @param {string[]} config.autocompleteFields - fields the query searches
 * @param {string} config.filterName - name of the filter group sent to the search
 * @param {object|(() => object)} [config.otherFilters] - extra filters, or a function returning them when the value
 *   is not ready until the search runs
 * @returns {(event: {query: string}, setFiltered: Function, setInputValue: Function) => void} the search, which
 *   passes the query to `setInputValue` and the suggestions to `setFiltered`
 */
export const buildSearchFn = (config) => (event, setFiltered, setInputValue) => {
	const searchService = new SearchService();
	setInputValue(event.query);
	const filter = buildAutocompleteFilter(event, config.autocompleteFields);
	const otherFilters = typeof config.otherFilters === 'function' ? config.otherFilters() : config.otherFilters;
	autocompleteSearch(searchService, config.endpoint, config.filterName, filter, setFiltered, otherFilters);
};
