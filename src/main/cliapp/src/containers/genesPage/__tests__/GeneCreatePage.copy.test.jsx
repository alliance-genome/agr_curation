import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import { button, FORM_LOAD_WAIT, section, storedGene } from './geneTestUtils';

const { getGene, createGene, getRows, replaceRows, navigate } = vi.hoisted(() => ({
	getGene: vi.fn(),
	createGene: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
	navigate: vi.fn(),
}));

vi.mock('../../../service/GeneService', async () =>
	(await import('./geneServiceMocks')).mockGeneService({ getGene, createGene })
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
vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const GeneCreatePage = (await import('../GeneCreatePage')).default;

const COPY_SOURCE_ENTRY = `/gene/create?from=${encodeURIComponent(storedGene.curie)}`;

const NCBI_PAGE = { id: 7, name: 'default', resourceDescriptor: { id: 3, prefix: 'NCBI_Gene' } };
const STORED_LISTS = {
	synonyms: [
		{ id: 31, internal: false, displayText: 'samdc', formatText: 'samdc', nameType: { name: 'systematic_name' } },
	],
	secondaryids: [{ id: 32, internal: false, secondaryId: 'FB:FBgn0000001' }],
	crossreferences: [
		{ id: 41, referencedCurie: 'NCBI_Gene:34396', displayName: 'NCBI_Gene:34396', resourceDescriptorPage: NCBI_PAGE },
	],
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={[COPY_SOURCE_ENTRY]}>
			<GeneCreatePage />
		</MemoryRouter>
	);

const bodyRows = (name) => within(section(name)).queryAllByRole('row').slice(1);

// The copy fills the full name too, whose row comes first and shares the symbol's input ids.
const symbolInput = () => section(/Symbol/).querySelector('#displayText');

const waitForCopy = () => waitFor(() => expect(symbolInput()).toHaveValue('SamDC'), FORM_LOAD_WAIT);

describe('<GeneCreatePage /> copying a gene', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('GeneCreateFormSettings');
		[getGene, createGene, getRows, replaceRows, navigate].forEach((stub) => stub.mockReset());
		getGene.mockResolvedValue({ data: { entity: storedGene } });
		getRows.mockImplementation((path) => Promise.resolve({ data: { entities: STORED_LISTS[path] ?? [] } }));
		replaceRows.mockImplementation((path, geneId, rows) => Promise.resolve({ data: { entities: rows } }));
		createGene.mockResolvedValue({ data: { entity: { ...storedGene, id: 4242, curie: 'AGRKB:105000000000042' } } });
	});

	it('fills the form from the gene named in the URL', async () => {
		const { container } = renderPage();

		await waitForCopy();
		expect(getGene).toHaveBeenCalledWith(storedGene.curie);
		// the autosuggests take the copied terms a render after the symbol row does
		await waitFor(
			() => expect(container.querySelector('input[aria-label="taxon"]')).toHaveValue('NCBITaxon:7227'),
			FORM_LOAD_WAIT
		);
		await waitFor(
			() => expect(container.querySelector('input[aria-label="geneType"]')).toHaveValue('SO:0001217'),
			FORM_LOAD_WAIT
		);
		// the GCRP cross reference keeps its curie, for the curator to review
		await waitFor(() =>
			expect(section('GCRP Cross Reference').querySelector('[aria-label="referencedCurie"]')).toHaveValue(
				storedGene.gcrpCrossReference.referencedCurie
			)
		);
	});

	it("copies the gene's synonyms, secondary IDs and cross references, keeping their values", async () => {
		renderPage();

		await waitForCopy();
		await waitFor(() => expect(bodyRows('Synonyms')).toHaveLength(1), FORM_LOAD_WAIT);
		await waitFor(() => expect(bodyRows('Secondary IDs')).toHaveLength(1), FORM_LOAD_WAIT);
		await waitFor(() => expect(bodyRows('Cross References')).toHaveLength(1), FORM_LOAD_WAIT);
		expect(screen.getAllByDisplayValue('samdc').length).toBeGreaterThan(0);
		expect(within(section('Secondary IDs')).getByDisplayValue('FB:FBgn0000001')).toBeInTheDocument();
		expect(section('Cross References').querySelector('[aria-label="referencedCurie"]')).toHaveValue('NCBI_Gene:34396');
	});

	it('creates the copy as a new gene, with new list rows', async () => {
		const user = userEvent.setup();
		renderPage();

		await waitForCopy();
		await waitFor(() => expect(bodyRows('Synonyms')).toHaveLength(1), FORM_LOAD_WAIT);
		await waitFor(() => expect(bodyRows('Cross References')).toHaveLength(1), FORM_LOAD_WAIT);
		await waitFor(() => expect(bodyRows('Secondary IDs')).toHaveLength(1), FORM_LOAD_WAIT);
		await user.clear(symbolInput());
		await user.type(symbolInput(), 'SamDC-copy');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createGene).toHaveBeenCalledTimes(1), FORM_LOAD_WAIT);
		const [payload] = createGene.mock.calls[0];
		expect(payload.id).toBeUndefined();
		expect(payload.curie).toBeUndefined();
		expect(payload.primaryExternalId).toBeUndefined();
		expect(payload.geneSymbol.id).toBeUndefined();
		expect(payload.geneSymbol.displayText).toBe('SamDC-copy');
		expect(payload.gcrpCrossReference.id).toBeUndefined();
		expect(payload.gcrpCrossReference.referencedCurie).toBe(storedGene.gcrpCrossReference.referencedCurie);
		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(3), FORM_LOAD_WAIT);
		const rowsSentTo = (path) => replaceRows.mock.calls.find(([calledPath]) => calledPath === path)[2];
		['synonyms', 'secondaryids', 'crossreferences'].forEach((path) => {
			expect(rowsSentTo(path)).toHaveLength(1);
			expect(rowsSentTo(path)[0].id).toBeUndefined();
			expect(replaceRows).toHaveBeenCalledWith(path, 4242, expect.any(Array));
		});
		expect(rowsSentTo('synonyms')[0].displayText).toBe('samdc');
		expect(rowsSentTo('secondaryids')[0].secondaryId).toBe('FB:FBgn0000001');
		expect(rowsSentTo('crossreferences')[0].referencedCurie).toBe('NCBI_Gene:34396');
	});

	it('copies no cross references into a hidden section', async () => {
		setLocalStorage('GeneCreateFormSettings', {
			selectedFormFields: [],
			orderedFormFields: ['Cross References', 'GCRP Cross Reference'],
			formSettingsKeyName: 'GeneCreateFormSettings',
		});
		renderPage();

		await waitForCopy();
		await waitFor(() => expect(getRows).toHaveBeenCalledWith('synonyms', storedGene.id), FORM_LOAD_WAIT);
		expect(getRows).not.toHaveBeenCalledWith('crossreferences', expect.anything());
	});

	it('says the copy is loading, not saving', async () => {
		let finishLoading;
		getGene.mockReturnValue(
			new Promise((resolve) => {
				finishLoading = () => resolve({ data: { entity: storedGene } });
			})
		);
		renderPage();

		expect(await screen.findByText('Loading gene to clone...')).toBeInTheDocument();
		expect(screen.queryByText('Saving in progress...')).not.toBeInTheDocument();

		finishLoading();
		await waitForCopy();
	});

	it('stays blank after clearing the copy', async () => {
		const user = userEvent.setup();
		renderPage();

		await waitForCopy();
		await waitFor(() => expect(bodyRows('Synonyms')).toHaveLength(1), FORM_LOAD_WAIT);
		await user.click(button('Clear'));

		await waitFor(() => expect(symbolInput()).toHaveValue(''), FORM_LOAD_WAIT);
		expect(bodyRows('Synonyms')).toHaveLength(0);
		expect(getGene).toHaveBeenCalledTimes(1);
	});

	it('does not look a gene up without one in the URL', async () => {
		renderWithClient(
			<MemoryRouter initialEntries={['/gene/create']}>
				<GeneCreatePage />
			</MemoryRouter>
		);

		expect(await screen.findByRole('heading', { name: 'Add Gene' })).toBeInTheDocument();
		expect(getGene).not.toHaveBeenCalled();
	});

	it('says so when the gene to copy cannot be loaded', async () => {
		getGene.mockRejectedValue(new Error('not found'));
		renderPage();

		expect(await screen.findByText(`Could not load gene ${storedGene.curie} to clone`)).toBeInTheDocument();
		expect(getRows).not.toHaveBeenCalled();
	});

	it('still starts from the copy when a list cannot be loaded', async () => {
		getRows.mockImplementation((path) =>
			path === 'synonyms'
				? Promise.reject(new Error('unavailable'))
				: Promise.resolve({ data: { entities: STORED_LISTS[path] ?? [] } })
		);
		renderPage();

		await waitForCopy();
		expect(
			await screen.findByText(`Could not load the synonyms of gene ${storedGene.primaryExternalId}`)
		).toBeInTheDocument();
		expect(bodyRows('Synonyms')).toHaveLength(0);
	});
});
