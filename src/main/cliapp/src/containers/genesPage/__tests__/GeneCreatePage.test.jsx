import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { renderWithClient } from '../../../tools/jest/utils';
import { button, FORM_LOAD_WAIT, NAVIGATION_WAIT, section } from './geneTestUtils';

const { createGene, saveGeneDetail, replaceRows, navigate } = vi.hoisted(() => ({
	createGene: vi.fn(),
	saveGeneDetail: vi.fn(),
	replaceRows: vi.fn(),
	navigate: vi.fn(),
}));

vi.mock('../../../service/GeneService', async () =>
	(await import('./geneServiceMocks')).mockGeneService({ createGene, saveGeneDetail })
);
vi.mock('../../../service/GeneSubResourceService', async () =>
	(await import('./geneServiceMocks')).mockGeneSubResourceService({ replaceRows })
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

// The gene as the API returns it from create: its own ids and minted curie on it and on its symbol.
const createdGene = {
	id: 4242,
	curie: 'AGRKB:101000000000042',
	type: 'Gene',
	taxon: { id: 9, curie: 'NCBITaxon:7227', name: 'Drosophila melanogaster' },
	geneType: { id: 11, curie: 'SO:0001217', name: 'protein_coding_gene' },
	internal: false,
	obsolete: false,
	geneSymbol: {
		id: 51,
		displayText: 'abc',
		formatText: 'abc',
		nameType: { name: 'nomenclature_symbol' },
		internal: false,
	},
};

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<GeneCreatePage />
		</BrowserRouter>
	);

const heading = (name) => screen.queryByRole('heading', { name });

const typeSymbol = async (user, text) => {
	const symbolSection = section(/Symbol/);
	await user.type(within(symbolSection).getAllByRole('textbox')[0], text);
};

const addSecondaryId = async (user, secondaryId) => {
	await user.click(button('Add Secondary ID'));
	await user.type(within(section('Secondary IDs')).getAllByRole('textbox')[0], secondaryId);
};

describe('<GeneCreatePage />', { timeout: 60000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('GeneCreateFormSettings');
		[createGene, saveGeneDetail, replaceRows, navigate].forEach((stub) => stub.mockReset());
		createGene.mockResolvedValue({ data: { entity: createdGene } });
		saveGeneDetail.mockResolvedValue({ data: { entity: createdGene } });
		replaceRows.mockImplementation((path, geneId, rows) => Promise.resolve({ data: { entities: rows } }));
	});

	it('renders the create form rather than the detail fields', async () => {
		renderPage();

		expect(heading('Add Gene')).toBeInTheDocument();
		await waitFor(() => expect(heading('Data Provider')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(heading('Curie')).not.toBeInTheDocument();
		expect(heading('Date Created')).not.toBeInTheDocument();
		['Clear', 'Cancel', 'Save & Close', 'Save & Add Another'].forEach((name) =>
			expect(button(name)).toBeInTheDocument()
		);
	});

	it('posts a new gene without the blank placeholders and opens it once saved', async () => {
		const user = userEvent.setup();
		renderPage();

		await typeSymbol(user, 'abc');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createGene).toHaveBeenCalledTimes(1));
		const [payload] = createGene.mock.calls[0];
		expect(payload.type).toBe('Gene');
		expect(payload.taxon).toBeUndefined();
		expect(payload.geneType).toBeUndefined();
		expect(payload.geneSymbol.displayText).toBe('abc');
		expect(await screen.findByText(`Gene ${createdGene.curie} created`)).toBeInTheDocument();
		await waitFor(() => expect(navigate).toHaveBeenCalledWith(`/gene/${createdGene.curie}`), NAVIGATION_WAIT);
		expect(replaceRows).not.toHaveBeenCalled();
	});

	it('shows the messages the API returns for a gene it rejects, and stays on the page', async () => {
		const user = userEvent.setup();
		createGene.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not create Gene',
					errorMessages: { taxon: 'Required field is empty', geneType: 'Required field is empty' },
				},
			},
		});
		renderPage();

		await user.click(button('Save & Close'));

		expect(await screen.findByText('Could not create Gene')).toBeInTheDocument();
		expect(screen.getAllByText('Required field is empty')).toHaveLength(2);
		expect(navigate).not.toHaveBeenCalled();
	});

	it('shows a symbol another gene of the taxon already uses on the symbol row and on Taxon', async () => {
		const user = userEvent.setup();
		const message = 'Another gene is already using this taxon/symbol combination';
		createGene.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not create Gene',
					errorMessages: { geneSymbol: `displayText - ${message}`, taxon: message },
					supplementalData: { errorMap: { geneSymbol: { displayText: message }, taxon: message } },
				},
			},
		});
		renderPage();

		await typeSymbol(user, 'abc');
		await user.click(button('Save & Close'));

		expect(await within(section(/Symbol/)).findByText(message)).toBeInTheDocument();
		expect(screen.getAllByText(message)).toHaveLength(2);
		expect(navigate).not.toHaveBeenCalled();
	});

	it("writes the edited lists to the new gene's id", async () => {
		const user = userEvent.setup();
		renderPage();

		await addSecondaryId(user, 'FB:FBgn0000001');
		await user.click(button('Save & Close'));

		await waitFor(() =>
			expect(replaceRows).toHaveBeenCalledWith(
				'secondaryids',
				createdGene.id,
				expect.arrayContaining([expect.objectContaining({ secondaryId: 'FB:FBgn0000001' })])
			)
		);
		await waitFor(() => expect(navigate).toHaveBeenCalledWith(`/gene/${createdGene.curie}`), NAVIGATION_WAIT);
	});

	it('keeps the curator on the page when a list fails, and saving again updates the gene rather than creating another', async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValueOnce({
			response: { status: 400, data: { errorMessage: 'Could not update SecondaryIds' } },
		});
		renderPage();

		await addSecondaryId(user, 'FB:FBgn0000001');
		await user.click(button('Save & Close'));

		expect(await screen.findByText(/Gene AGRKB:101000000000042 was created/)).toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();

		await waitFor(() => expect(button('Save & Close')).toBeEnabled());
		await user.click(button('Save & Close'));

		await waitFor(() => expect(saveGeneDetail).toHaveBeenCalledTimes(1));
		expect(saveGeneDetail.mock.calls[0][0].id).toBe(createdGene.id);
		expect(createGene).toHaveBeenCalledTimes(1);
		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(2));
		await waitFor(() => expect(navigate).toHaveBeenCalledWith(`/gene/${createdGene.curie}`), NAVIGATION_WAIT);
	});

	it('saves again through the existing gene after a retry is rejected, never creating a second one', async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValueOnce({
			response: { status: 400, data: { errorMessage: 'Could not update SecondaryIds' } },
		});
		saveGeneDetail.mockRejectedValueOnce({
			response: { status: 400, data: { errorMessage: 'Could not update Gene' } },
		});
		renderPage();

		await addSecondaryId(user, 'FB:FBgn0000001');
		await user.click(button('Save & Close'));
		expect(await screen.findByText(/Gene AGRKB:101000000000042 was created/)).toBeInTheDocument();

		await waitFor(() => expect(button('Save & Close')).toBeEnabled());
		await user.click(button('Save & Close'));
		expect(await screen.findByText('Could not update Gene')).toBeInTheDocument();

		await waitFor(() => expect(button('Save & Close')).toBeEnabled());
		await user.click(button('Save & Close'));

		await waitFor(() => expect(saveGeneDetail).toHaveBeenCalledTimes(2));
		expect(createGene).toHaveBeenCalledTimes(1);
		await waitFor(() => expect(navigate).toHaveBeenCalledWith(`/gene/${createdGene.curie}`), NAVIGATION_WAIT);
	});

	it('keeps the saved values on Save & Add Another, and creates the next gene from them without their ids', async () => {
		const user = userEvent.setup();
		renderPage();

		await typeSymbol(user, 'abc');
		await user.click(button('Save & Add Another'));

		// The taxon was never entered, so it shows only once the form takes on the saved gene.
		await waitFor(() => expect(screen.getByDisplayValue(createdGene.taxon.curie)).toBeInTheDocument());
		expect(navigate).not.toHaveBeenCalled();

		await waitFor(() => expect(button('Save & Add Another')).toBeEnabled());
		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createGene).toHaveBeenCalledTimes(2));
		const [nextGene] = createGene.mock.calls[1];
		expect(nextGene.id).toBeUndefined();
		expect(nextGene.geneSymbol.id).toBeUndefined();
		expect(nextGene.geneSymbol.displayText).toBe('abc');
		expect(nextGene.taxon.curie).toBe(createdGene.taxon.curie);
		expect(saveGeneDetail).not.toHaveBeenCalled();
	});

	it("starts the next gene's synonyms and secondary IDs from the saved gene's", async () => {
		const user = userEvent.setup();
		// rows come back stored, with their own ids, which the next gene's copies must not carry
		replaceRows.mockImplementation((path, geneId, rows) =>
			Promise.resolve({ data: { entities: rows.map((row, index) => ({ ...row, id: 900 + index })) } })
		);
		renderPage();

		await user.click(button('Add Synonym'));
		await addSecondaryId(user, 'FB:FBgn0000001');
		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(2));
		await waitFor(() => expect(button('Save & Add Another')).toBeEnabled());

		// the next gene's save writes the carried-over rows, as new rows
		await user.click(button('Save & Add Another'));
		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(4));
		const nextSave = replaceRows.mock.calls.slice(2);
		const rowsSentTo = (path) => nextSave.find(([calledPath]) => calledPath === path)[2];
		expect(rowsSentTo('synonyms')).toHaveLength(1);
		expect(rowsSentTo('synonyms')[0].id).toBeUndefined();
		expect(rowsSentTo('secondaryids')).toEqual([expect.objectContaining({ secondaryId: 'FB:FBgn0000001' })]);
		expect(rowsSentTo('secondaryids')[0].id).toBeUndefined();
	});

	it('clears the form', async () => {
		const user = userEvent.setup();
		renderPage();

		await typeSymbol(user, 'abc');
		await addSecondaryId(user, 'FB:FBgn0000001');
		await user.click(button('Clear'));

		await waitFor(() =>
			within(section(/Symbol/))
				.getAllByRole('textbox')
				.forEach((textbox) => expect(textbox).toHaveValue(''))
		);
		expect(within(section('Secondary IDs')).queryByRole('table')).not.toBeInTheDocument();
	});

	it('returns to the genes table on cancel', async () => {
		const user = userEvent.setup();
		renderPage();

		await user.click(button('Cancel'));

		expect(navigate).toHaveBeenCalledWith('/genes');
	});
});
