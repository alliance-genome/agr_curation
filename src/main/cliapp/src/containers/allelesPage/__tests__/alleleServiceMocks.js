/**
 * Module factories for the services the allele detail and create pages build, for `vi.mock`.
 *
 * BaseAuthService builds no client without a cognito token in local storage, so msw has nothing to intercept
 * and the page tests stub the services directly. `vi.mock` is hoisted, so each test file still makes its own
 * call and loads these inside the factory:
 *
 * ```js
 * vi.mock('../../../service/ReferenceService', async () =>
 * 	(await import('./alleleServiceMocks')).mockReferenceService({ get: getReferencesForAllele })
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
 * @param {Object} [functions]
 * @param {Function} [functions.get] stands in for `getCrossReferencesForAllele`
 * @param {Function} [functions.replace] stands in for `replaceCrossReferencesForAllele`
 */
export const mockCrossReferenceService = ({ get, replace } = {}) => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = get ?? vi.fn(emptyListResponse);
		replaceCrossReferencesForAllele = replace ?? vi.fn(emptyListResponse);
	},
});

/**
 * @param {Object} [functions]
 * @param {Function} [functions.get] stands in for `getReferencesForAllele`
 * @param {Function} [functions.replace] stands in for `replaceReferencesForAllele`
 */
export const mockReferenceService = ({ get, replace } = {}) => ({
	ReferenceService: class {
		getReferencesForAllele = get ?? vi.fn(emptyListResponse);
		replaceReferencesForAllele = replace ?? vi.fn(emptyListResponse);
	},
});

/**
 * @param {Object} [functions]
 * @param {Function} [functions.get] stands in for `getGeneAssociationsForAllele`
 * @param {Function} [functions.replace] stands in for `replaceGeneAssociationsForAllele`
 */
export const mockGeneAssociationService = ({ get, replace } = {}) => ({
	AlleleGeneAssociationService: class {
		getGeneAssociationsForAllele = get ?? vi.fn(emptyListResponse);
		replaceGeneAssociationsForAllele = replace ?? vi.fn(emptyListResponse);
	},
});
