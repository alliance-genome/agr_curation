import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FORM_LOAD_WAIT, geneRejection, renderAtGeneRoute, section, storedGene } from './geneTestUtils';

const { getGene, saveGeneDetail, getResourceDescriptor } = vi.hoisted(() => ({
	getGene: vi.fn(),
	saveGeneDetail: vi.fn(),
	getResourceDescriptor: vi.fn(),
}));

vi.mock('../../../service/GeneService', async () =>
	(await import('./geneServiceMocks')).mockGeneService({ getGene, saveGeneDetail })
);
vi.mock('../../../service/GeneSubResourceService', async () =>
	(await import('./geneServiceMocks')).mockGeneSubResourceService()
);
vi.mock('../../../service/ResourceDescriptorService', async () =>
	(await import('./geneServiceMocks')).mockResourceDescriptorService(getResourceDescriptor)
);
vi.mock('../../../service/SearchService', async () =>
	(await import('../../allelesPage/__tests__/alleleServiceMocks')).mockSearchService()
);

const GeneDetailPage = (await import('../GeneDetailPage')).default;

const saveButton = () => screen.getByRole('button', { name: 'Save' });
const gcrpCurie = () => section('GCRP Cross Reference').querySelector('[aria-label="referencedCurie"]');

const renderLoaded = async () => {
	const result = renderAtGeneRoute(<GeneDetailPage />);
	await waitFor(() => expect(gcrpCurie()).toHaveValue('UniProtKB:P91931'), FORM_LOAD_WAIT);
	return result;
};

const removeGcrpCrossReference = async (user) =>
	user.click(section('GCRP Cross Reference').querySelector('.pi-trash').closest('button'));

describe('<GeneDetailPage />', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('GeneDetailFormSettings');
		getGene.mockReset();
		saveGeneDetail.mockReset();
		getResourceDescriptor.mockReset();
		getGene.mockResolvedValue({ data: { entity: storedGene } });
		saveGeneDetail.mockResolvedValue({ data: { entity: { ...storedGene, gcrpCrossReference: null } } });
		getResourceDescriptor.mockImplementation((id) =>
			Promise.resolve({
				data: { entity: { id, prefix: 'UniProtKB', resourcePages: [{ id: 41750700, name: 'default' }] } },
			})
		);
	});

	it('reads the gene by the identifier in its route and heads the page with its symbol and identifier', async () => {
		await renderLoaded();

		expect(getGene).toHaveBeenCalledWith('FB:FBgn0019932');
		expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Gene: SamDC (FB:FBgn0019932)');
	});

	it('offers Save alone, and only once something is edited', async () => {
		await renderLoaded();

		expect(saveButton()).toBeDisabled();
		['New', 'Clone', 'Delete'].forEach((name) =>
			expect(screen.queryByRole('button', { name })).not.toBeInTheDocument()
		);
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it("reads the GCRP cross reference's descriptor, so the loaded gene has nothing pending", async () => {
		await renderLoaded();

		expect(getResourceDescriptor).toHaveBeenCalledWith(41750690);
		expect(saveButton()).toBeDisabled();
	});

	it('marks an edited section as pending and enables Save', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await removeGcrpCrossReference(user);

		expect(within(section('GCRP Cross Reference')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(saveButton()).toBeEnabled();
	});

	it('saves the gene through the detail endpoint and says so', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await removeGcrpCrossReference(user);
		await user.click(saveButton());

		await waitFor(() => expect(saveGeneDetail).toHaveBeenCalledTimes(1));
		const [payload] = saveGeneDetail.mock.calls[0];
		expect(payload).toEqual(expect.objectContaining({ id: storedGene.id, gcrpCrossReference: null }));
		expect(payload.taxon.curie).toBe('NCBITaxon:7227');
		expect(payload.geneType.curie).toBe('SO:0001217');
		expect(await screen.findByText('Gene Saved')).toBeInTheDocument();
		await waitFor(() => expect(saveButton()).toBeDisabled());
	});

	it('shows why a rejected save failed on the cells at fault, keeping the edit', async () => {
		const user = userEvent.setup();
		saveGeneDetail.mockRejectedValue(geneRejection);
		await renderLoaded();

		await user.click(section('GCRP Cross Reference').querySelector('[aria-label="referencedCurie"]'));
		await user.type(gcrpCurie(), 'x');
		await user.click(saveButton());

		expect(await screen.findByText(/Could not update Gene/)).toBeInTheDocument();
		const gcrpSection = within(section('GCRP Cross Reference'));
		expect(gcrpSection.getByText('Invalid')).toBeInTheDocument();
		expect(gcrpSection.queryByText('referencedCurie - Invalid')).not.toBeInTheDocument();
		expect(gcrpCurie()).toHaveValue('UniProtKB:P91931x');
		expect(saveButton()).toBeEnabled();
	});

	it('shows a GCRP error naming a field with no cell beside the Add button', async () => {
		const user = userEvent.setup();
		saveGeneDetail.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update Gene: [FB:FBgn0019932]',
					errorMessages: { gcrpCrossReference: 'id - Not a valid entry' },
				},
			},
		});
		await renderLoaded();

		await user.type(gcrpCurie(), 'x');
		await user.click(saveButton());

		expect(await within(section('GCRP Cross Reference')).findByText('id - Not a valid entry')).toBeInTheDocument();
	});

	it('says no gene has an identifier the API does not know', async () => {
		getGene.mockResolvedValue({ data: {} });
		renderAtGeneRoute(<GeneDetailPage />);

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Gene not found'),
			FORM_LOAD_WAIT
		);
		expect(screen.getByText('No gene has the identifier FB:FBgn0019932')).toBeInTheDocument();
		expect(screen.getByRole('link', { name: 'Back to the gene table' })).toHaveAttribute('href', '/genes');
		expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
	});
});
