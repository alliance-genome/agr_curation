import React from 'react';
import { act, fireEvent, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FORM_LOAD_WAIT, renderAtGeneRoute, section, storedGene } from './geneTestUtils';

const { getGene, saveGeneDetail, getRows, replaceRows } = vi.hoisted(() => ({
	getGene: vi.fn(),
	saveGeneDetail: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
}));

vi.mock('../../../service/GeneService', async () =>
	(await import('./geneServiceMocks')).mockGeneService({ getGene, saveGeneDetail })
);
vi.mock('../../../service/GeneSubResourceService', async () =>
	(await import('./geneServiceMocks')).mockGeneSubResourceService({ getRows, replaceRows })
);
vi.mock('../../../service/ResourceDescriptorService', async () =>
	(await import('./geneServiceMocks')).mockResourceDescriptorService()
);
vi.mock('../../../service/SearchService', async () =>
	(await import('../../allelesPage/__tests__/alleleServiceMocks')).mockSearchService()
);

const GeneDetailPage = (await import('../GeneDetailPage')).default;

const SYNONYMS = [
	{ id: 31, internal: false, displayText: 'samdc', formatText: 'samdc', nameType: { name: 'systematic_name' } },
	{ id: 32, internal: false, displayText: 'CG5029', formatText: 'CG5029', nameType: { name: 'systematic_name' } },
];
const NCBI_PAGE = { id: 7, name: 'default', resourceDescriptor: { id: 3, prefix: 'NCBI_Gene' } };
const CROSS_REFERENCES = [
	{ id: 41, referencedCurie: 'NCBI_Gene:34396', displayName: 'NCBI_Gene:34396', resourceDescriptorPage: NCBI_PAGE },
	{ id: 42, referencedCurie: 'NCBI_Gene:34397', displayName: 'NCBI_Gene:34397', resourceDescriptorPage: NCBI_PAGE },
];
const STORED_LISTS = { synonyms: SYNONYMS, crossreferences: CROSS_REFERENCES };

const bodyRows = (name) => within(section(name)).getAllByRole('row').slice(1);

const renderLoaded = async () => {
	renderAtGeneRoute(<GeneDetailPage />);
	await waitFor(() => expect(bodyRows('Synonyms')).toHaveLength(2), FORM_LOAD_WAIT);
	await waitFor(() => expect(bodyRows('Cross References')).toHaveLength(2), FORM_LOAD_WAIT);
	// Lets the rows' editors settle before they are clicked.
	await act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});
};

const deleteFirstRow = async (user, name) => user.click(bodyRows(name)[0].querySelector('.pi-trash').closest('button'));

describe('<GeneDetailPage /> sub-resource lists', { timeout: 60000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('GeneDetailFormSettings');
		[getGene, saveGeneDetail, getRows, replaceRows].forEach((stub) => stub.mockReset());
		getGene.mockResolvedValue({ data: { entity: storedGene } });
		saveGeneDetail.mockResolvedValue({ data: { entity: storedGene } });
		getRows.mockImplementation((path) => Promise.resolve({ data: { entities: STORED_LISTS[path] ?? [] } }));
		replaceRows.mockImplementation((path, geneId, rows) => Promise.resolve({ data: { entities: rows } }));
	});

	it("reads each of the gene's lists by its id", async () => {
		await renderLoaded();

		['synonyms', 'secondaryids', 'relatednotes', 'crossreferences'].forEach((path) =>
			expect(getRows).toHaveBeenCalledWith(path, storedGene.id)
		);
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});

	it("saves one list alone with its section's own Save", async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await deleteFirstRow(user, 'Synonyms');
		await user.click(screen.getByRole('button', { name: 'Save Synonyms' }));

		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(1));
		const [path, geneId, rows] = replaceRows.mock.calls[0];
		expect([path, geneId]).toEqual(['synonyms', storedGene.id]);
		expect(rows.map((row) => row.id)).toEqual([32]);
		expect(saveGeneDetail).not.toHaveBeenCalled();
		expect(await screen.findByText('Synonyms Saved')).toBeInTheDocument();
	});

	it('writes the gene and each edited list from the page Save, showing the progress of each', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await deleteFirstRow(user, 'Synonyms');
		fireEvent.click(screen.getByRole('button', { name: 'Save' }));
		const progress = screen.getByRole('status', { name: 'Save progress' });

		expect(
			within(progress)
				.getAllByRole('listitem')
				.map((item) => item.textContent.split(':')[0])
		).toEqual(['Gene', 'Synonyms']);
		await waitFor(() => expect(replaceRows).toHaveBeenCalledWith('synonyms', storedGene.id, expect.any(Array)));
		expect(saveGeneDetail).toHaveBeenCalledTimes(1);
		expect(replaceRows).toHaveBeenCalledTimes(1);
		expect(await screen.findByText('Gene Saved')).toBeInTheDocument();
	});

	it("shows a cross reference the API rejects as the GCRP's duplicate on its row", async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update CrossReferences for: [4636168]',
					supplementalData: {
						errorMap: { crossReferences: { 0: { referencedCurie: 'Duplicate (GCRP cross reference)' } } },
					},
				},
			},
		});
		await renderLoaded();

		await deleteFirstRow(user, 'Cross References');
		await user.click(screen.getByRole('button', { name: 'Save Cross References' }));

		expect(
			await within(section('Cross References')).findByText('Duplicate (GCRP cross reference)')
		).toBeInTheDocument();
		expect(bodyRows('Cross References')).toHaveLength(1);
	});
});
