/**
 * Module factories for the services the gene detail and create pages build, for `vi.mock`. The services are stubbed directly
 * because BaseAuthService builds no client without a token, and each test file makes its own hoisted `vi.mock` call
 * that loads these inside the factory:
 *
 * ```js
 * vi.mock('../../../service/GeneService', async () =>
 * 	(await import('./geneServiceMocks')).mockGeneService({ getGene })
 * );
 * ```
 *
 * This module imports nothing, so loading it inside a factory cannot pull in a module that is itself mocked.
 */

const emptyListResponse = () => Promise.resolve({ data: { entities: [] } });

/**
 * @param {Object} [functions]
 * @param {Function} [functions.getGene]
 * @param {Function} [functions.saveGeneDetail]
 * @param {Function} [functions.createGene]
 * @param {Function} [functions.deleteGene]
 */
export const mockGeneService = ({ getGene, saveGeneDetail, createGene, deleteGene } = {}) => ({
	GeneService: class {
		getGene = getGene ?? vi.fn();
		saveGeneDetail = saveGeneDetail ?? vi.fn();
		createGene = createGene ?? vi.fn();
		deleteGene = deleteGene ?? vi.fn();
	},
});

/**
 * Routes each list's reads and writes to `getRows(path, geneId)` and `replaceRows(path, geneId, rows)`, so a test can
 * stub and check any list by its path under the gene. A list not stubbed reads and writes as empty.
 *
 * @param {Object} [functions]
 * @param {Function} [functions.getRows]
 * @param {Function} [functions.replaceRows]
 */
export const mockGeneSubResourceService = ({ getRows, replaceRows } = {}) => ({
	GeneSubResourceService: class {
		getList = (geneId, path) => (getRows ?? emptyListResponse)(path, geneId);
		replaceList = (geneId, path, rows) => (replaceRows ?? emptyListResponse)(path, geneId, rows);
	},
});

/**
 * The descriptor the GCRP cross reference's page belongs to, read in full when a gene is shown.
 *
 * @param {Function} [getResourceDescriptor]
 */
export const mockResourceDescriptorService = (getResourceDescriptor) => ({
	ResourceDescriptorService: class {
		getResourceDescriptor =
			getResourceDescriptor ??
			vi.fn((id) =>
				Promise.resolve({
					data: { entity: { id, prefix: 'UniProtKB', resourcePages: [{ id: 41750700, name: 'default' }] } },
				})
			);
	},
});
