import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FORM_LOAD_WAIT, renderAtGeneRoute, section, storedGene } from './geneTestUtils';

const { getGene, saveGeneDetail, deleteGene, navigate } = vi.hoisted(() => ({
	getGene: vi.fn(),
	saveGeneDetail: vi.fn(),
	deleteGene: vi.fn(),
	navigate: vi.fn(),
}));

vi.mock('../../../service/GeneService', async () =>
	(await import('./geneServiceMocks')).mockGeneService({ getGene, saveGeneDetail, deleteGene })
);
vi.mock('../../../service/GeneSubResourceService', async () =>
	(await import('./geneServiceMocks')).mockGeneSubResourceService()
);
vi.mock('../../../service/ResourceDescriptorService', async () =>
	(await import('./geneServiceMocks')).mockResourceDescriptorService()
);
vi.mock('../../../service/SearchService', async () =>
	(await import('../../allelesPage/__tests__/alleleServiceMocks')).mockSearchService()
);
vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const GeneDetailPage = (await import('../GeneDetailPage')).default;

const gcrpCurie = () => section('GCRP Cross Reference').querySelector('[aria-label="referencedCurie"]');

const renderLoaded = async () => {
	renderAtGeneRoute(<GeneDetailPage />);
	await waitFor(() => expect(gcrpCurie()).toHaveValue('UniProtKB:P91931'), FORM_LOAD_WAIT);
};

const dialog = () => screen.getByRole('dialog');

const confirmDeletion = async (user) => {
	await user.click(screen.getByRole('button', { name: 'Delete' }));
	await user.click(within(dialog()).getByRole('checkbox'));
	await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));
};

describe('<GeneDetailPage /> header actions', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('GeneDetailFormSettings');
		[getGene, saveGeneDetail, deleteGene, navigate].forEach((stub) => stub.mockReset());
		getGene.mockResolvedValue({ data: { entity: storedGene } });
		saveGeneDetail.mockImplementation((gene) => Promise.resolve({ data: { entity: gene } }));
		deleteGene.mockResolvedValue({ isSuccess: true, isError: false });
	});

	it('deletes the gene once confirmed and returns to the genes table', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await confirmDeletion(user);

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/genes'), FORM_LOAD_WAIT);
		expect(deleteGene.mock.calls[0][0].curie).toEqual(storedGene.curie);
	});

	it('stays on the gene and says why when the deletion is refused', async () => {
		const user = userEvent.setup();
		deleteGene.mockResolvedValue({ isSuccess: false, isError: true, message: 'Gene is in use and cannot be deleted' });
		await renderLoaded();

		await confirmDeletion(user);

		expect(await screen.findByText('Gene is in use and cannot be deleted')).toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('deprecates the gene as last saved, leaving unsaved edits on the form', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		await user.click(section('GCRP Cross Reference').querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		expect(await screen.findByText('Gene Deprecated')).toBeInTheDocument();
		const [deprecated] = saveGeneDetail.mock.calls[0];
		expect(deprecated.obsolete).toBe(true);
		expect(deprecated.curie).toEqual(storedGene.curie);
		expect(deprecated.gcrpCrossReference.referencedCurie).toEqual('UniProtKB:P91931');
		expect(deleteGene).not.toHaveBeenCalled();

		// the removal is still on the form, now alongside the obsolete flag
		await user.click(screen.getByRole('button', { name: 'Save' }));
		await waitFor(() => expect(saveGeneDetail).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		const [saved] = saveGeneDetail.mock.calls[1];
		expect(saved.obsolete).toBe(true);
		expect(saved.gcrpCrossReference).toBeNull();
	});

	it('covers the page with the plain saving overlay while the deprecation is written', async () => {
		const user = userEvent.setup();
		let finishDeprecation;
		await renderLoaded();
		saveGeneDetail.mockReturnValue(
			new Promise((resolve) => {
				finishDeprecation = () => resolve({ data: { entity: { ...storedGene, obsolete: true } } });
			})
		);

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		expect(await screen.findByText('Saving in progress...')).toBeInTheDocument();
		expect(screen.queryByRole('status', { name: 'Save progress' })).not.toBeInTheDocument();

		finishDeprecation();
		await waitFor(() => expect(screen.queryByText('Saving in progress...')).not.toBeInTheDocument(), FORM_LOAD_WAIT);
	});

	it('leaves the form as it was when the deprecation is refused', async () => {
		const user = userEvent.setup();
		saveGeneDetail.mockRejectedValueOnce({ response: { data: { errorMessage: 'Gene could not be saved' } } });
		await renderLoaded();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		expect(await screen.findByText('Gene not deprecated:')).toBeInTheDocument();
		expect(screen.getByText('Gene could not be saved')).toBeInTheDocument();
		expect(screen.queryByText('Gene Deprecated')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
	});
});
