import { waitFor } from '@testing-library/react';
import { buildSearchFn } from '../buildSearchFn';
import { SearchService } from '../../../../../service/SearchService';

vi.mock('../../../../../service/SearchService');

const QUERY_FILTER = {
	curie: { queryString: 'park', tokenOperator: 'AND', useKeywordFields: true },
	name: { queryString: 'park', tokenOperator: 'AND', useKeywordFields: true },
};
const OBSOLETE_FILTER = { obsoleteFilter: { obsolete: { queryString: false } } };
const TAXON_FILTER = { taxonFilter: { 'taxon.curie': { queryString: 'NCBITaxon:9606' } } };

const config = (otherFilters) => ({
	endpoint: 'doterm',
	autocompleteFields: ['curie', 'name'],
	filterName: 'diseaseFilter',
	otherFilters,
});

describe('buildSearchFn', () => {
	beforeEach(() => {
		SearchService.prototype.search.mockReset();
		SearchService.prototype.search.mockResolvedValue({ results: [{ curie: 'DOID:14330' }] });
	});

	it('passes the query to setInputValue and the results to setFiltered', async () => {
		const setFiltered = vi.fn();
		const setInputValue = vi.fn();

		buildSearchFn(config())({ query: 'park' }, setFiltered, setInputValue);

		expect(setInputValue).toHaveBeenCalledWith('park');
		expect(SearchService.prototype.search).toHaveBeenCalledWith('doterm', 15, 0, [], {
			diseaseFilter: QUERY_FILTER,
			...OBSOLETE_FILTER,
		});
		await waitFor(() => expect(setFiltered).toHaveBeenCalledWith([{ curie: 'DOID:14330' }]));
	});

	it('sends static extra filters with the query', () => {
		buildSearchFn(config(TAXON_FILTER))({ query: 'park' }, vi.fn(), vi.fn());

		expect(SearchService.prototype.search).toHaveBeenCalledWith('doterm', 15, 0, [], {
			diseaseFilter: QUERY_FILTER,
			...TAXON_FILTER,
			...OBSOLETE_FILTER,
		});
	});

	it('works out function extra filters each time it searches', () => {
		const otherFilters = vi.fn().mockReturnValueOnce(TAXON_FILTER).mockReturnValueOnce({});
		const search = buildSearchFn(config(otherFilters));
		expect(otherFilters).not.toHaveBeenCalled();

		search({ query: 'park' }, vi.fn(), vi.fn());
		search({ query: 'park' }, vi.fn(), vi.fn());

		expect(SearchService.prototype.search.mock.calls.map((call) => call[4])).toEqual([
			{ diseaseFilter: QUERY_FILTER, ...TAXON_FILTER, ...OBSOLETE_FILTER },
			{ diseaseFilter: QUERY_FILTER, ...OBSOLETE_FILTER },
		]);
	});
});
