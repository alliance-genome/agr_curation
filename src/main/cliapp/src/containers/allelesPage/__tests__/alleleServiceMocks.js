/**
 * Module factories for the services the allele detail and create pages build, for `vi.mock`.
 *
 * BaseAuthService builds no client without a cognito token in local storage, so msw has nothing to intercept
 * and the page tests stub the services directly. `vi.mock` is hoisted, so each test file still makes its own
 * call and loads these inside the factory:
 *
 * ```js
 * vi.mock('../../../service/SearchService', async () =>
 * 	(await import('./alleleServiceMocks')).mockSearchService({ search })
 * );
 * ```
 *
 * A factory runs when the page first imports the service, so the functions passed to it must exist by then:
 * declare them with `vi.hoisted`, or above the page's dynamic import. A search or sub-resource function not passed
 * resolves as an empty result; an AlleleService function not passed returns undefined.
 *
 * This module imports nothing, so loading it inside a factory cannot pull in a module that is itself mocked.
 */

export const emptyListResponse = () => Promise.resolve({ data: { entities: [] } });

export const emptySearchResponse = () => Promise.resolve({ results: [], totalResults: 0 });

/**
 * @param {Object} [functions]
 * @param {Function} [functions.getAllele]
 * @param {Function} [functions.createAllele]
 * @param {Function} [functions.saveAlleleDetail]
 * @param {Function} [functions.deleteAllele]
 */
export const mockAlleleService = ({ getAllele, createAllele, saveAlleleDetail, deleteAllele } = {}) => ({
	AlleleService: class {
		getAllele = getAllele ?? vi.fn();
		createAllele = createAllele ?? vi.fn();
		saveAlleleDetail = saveAlleleDetail ?? vi.fn();
		deleteAllele = deleteAllele ?? vi.fn();
	},
});

/**
 * Typing into an autocomplete fires PrimeReact's debounced completeMethod, which builds its own SearchService.
 * Left unstubbed it reaches ApiClient after the test has finished and throws in a timer, failing whichever test
 * happens to run next.
 *
 * @param {Object} [functions]
 * @param {Function} [functions.search]
 */
export const mockSearchService = ({ search } = {}) => ({
	SearchService: class {
		search = search ?? vi.fn(emptySearchResponse);
		find = vi.fn(emptySearchResponse);
	},
});

/**
 * Routes each list's reads and writes to stand-ins of its own, keyed by the list's path under the allele, so a test
 * can stub and check one list without the others:
 *
 * ```js
 * mockSubResourceService({ references: { get: getReferencesForAllele, replace: replaceReferencesForAllele } })
 * ```
 *
 * `get` is called with the allele's id and `replace` with the id and the rows.
 *
 * @param {Object<string, {get?: Function, replace?: Function}>} [lists]
 */
export const mockSubResourceService = (lists = {}) => ({
	AlleleSubResourceService: class {
		getList = (alleleId, path) => (lists[path]?.get ?? emptyListResponse)(alleleId);
		replaceList = (alleleId, path, rows) => (lists[path]?.replace ?? emptyListResponse)(alleleId, rows);
	},
});
