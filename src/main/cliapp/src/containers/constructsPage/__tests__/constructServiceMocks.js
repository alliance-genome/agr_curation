/**
 * Module factories for the services the construct detail page builds, for `vi.mock`. The services are stubbed
 * directly because BaseAuthService builds no client without a token, and each test file makes its own hoisted
 * `vi.mock` call that loads these inside the factory:
 *
 * ```js
 * vi.mock('../../../service/ConstructService', async () =>
 * 	(await import('./constructServiceMocks')).mockConstructService({ getConstruct })
 * );
 * ```
 *
 * This module imports nothing, so loading it inside a factory cannot pull in a module that is itself mocked.
 */

const emptyListResponse = () => Promise.resolve({ data: { entities: [] } });

/**
 * @param {Object} [functions]
 * @param {Function} [functions.getConstruct]
 * @param {Function} [functions.saveConstructDetail]
 */
export const mockConstructService = ({ getConstruct, saveConstructDetail } = {}) => ({
	ConstructService: class {
		getConstruct = getConstruct ?? vi.fn();
		saveConstructDetail = saveConstructDetail ?? vi.fn();
	},
});

/**
 * Routes each list's reads and writes to `getRows(path, constructId)` and `replaceRows(path, constructId, rows)`, so
 * a test can stub and check any list by its path under the construct. A list not stubbed reads and writes as empty.
 *
 * @param {Object} [functions]
 * @param {Function} [functions.getRows]
 * @param {Function} [functions.replaceRows]
 */
export const mockConstructSubResourceService = ({ getRows, replaceRows } = {}) => ({
	ConstructSubResourceService: class {
		getList = (constructId, path) => (getRows ?? emptyListResponse)(path, constructId);
		replaceList = (constructId, path, rows) => (replaceRows ?? emptyListResponse)(path, constructId, rows);
	},
});
