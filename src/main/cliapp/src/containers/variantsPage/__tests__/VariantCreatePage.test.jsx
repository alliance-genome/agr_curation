import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createVariant = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/VariantService', () => ({
	VariantService: class {
		createVariant = createVariant;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const VariantCreatePage = (await import('../VariantCreatePage')).default;

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<VariantCreatePage />
		</BrowserRouter>
	);

const heading = (name) => screen.queryByRole('heading', { name });
const button = (name) => screen.getByRole('button', { name });

// The variant as the API returns it from create: its own ids and audit fields on it.
const savedVariant = {
	id: 4242,
	curie: 'AGRKB:103000000000001',
	type: 'Variant',
	taxon: { id: 9, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	internal: false,
	obsolete: false,
	dateCreated: '2026-10-23T00:00:00Z',
	createdBy: { uniqueId: 'curator1' },
	relatedNotes: [
		{
			id: 31,
			freeText: 'a note',
			noteType: { name: 'comment' },
			internal: false,
			dateCreated: '2026-10-23T00:00:00Z',
		},
	],
	references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
	synonyms: ['var-1'],
};

describe('<VariantCreatePage />', () => {
	beforeEach(() => {
		createVariant.mockReset();
		createVariant.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:103000000000001' } } });
		navigate.mockReset();
		window.localStorage.removeItem('VariantCreateFormSettings');
	});

	it('Renders the create form rather than the detail fields', async () => {
		await renderPage();

		expect(heading('Add Variant')).toBeInTheDocument();
		expect(heading('Taxon')).toBeInTheDocument();
		expect(heading('Variant Type')).toBeInTheDocument();
		expect(heading('Variant Status')).toBeInTheDocument();
		expect(heading('Source General Consequence')).toBeInTheDocument();
		expect(heading('Internal')).toBeInTheDocument();
		expect(heading('Curie')).not.toBeInTheDocument();
		expect(heading('Primary External ID')).not.toBeInTheDocument();
		expect(heading('Cross References')).not.toBeInTheDocument();
		expect(heading('Date Created')).not.toBeInTheDocument();
	});

	it('Offers all four actions', async () => {
		await renderPage();

		expect(button('Clear')).toBeInTheDocument();
		expect(button('Cancel')).toBeInTheDocument();
		expect(button('Save & Close')).toBeInTheDocument();
		expect(button('Save & Add Another')).toBeInTheDocument();
	});

	it('Sends an empty form to the API and shows the messages it returns', async () => {
		const user = userEvent.setup();
		createVariant.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not create Variant',
					errorMessages: { taxon: 'Required field is empty', variantType: 'Required field is empty' },
				},
			},
		});

		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(createVariant).toHaveBeenCalled());
		await waitFor(() => {
			expect(screen.getAllByText('Required field is empty')).toHaveLength(2);
		});
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Clears a populated field', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		const taxon = container.querySelector('input[name="taxon-input"]');
		await user.type(taxon, 'NCBITaxon:6239');
		expect(taxon).toHaveValue('NCBITaxon:6239');

		await user.click(button('Clear'));

		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('');
		});
	});

	it('Posts a payload without the blank taxon placeholder and opens the new variant', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(createVariant).toHaveBeenCalled());

		const payload = createVariant.mock.calls[0][0];
		expect(payload.type).toEqual('Variant');
		expect(payload).not.toHaveProperty('taxon');
		expect(payload).not.toHaveProperty('primaryExternalId');
		expect(payload).not.toHaveProperty('curie');

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/variant/AGRKB:103000000000001'));
	});

	it('Sends the typed taxon', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.type(container.querySelector('input[name="taxon-input"]'), 'NCBITaxon:6239');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createVariant).toHaveBeenCalled());
		expect(createVariant.mock.calls[0][0].taxon).toEqual({ curie: 'NCBITaxon:6239' });
	});

	it('Keeps the saved values on save and add another', async () => {
		const user = userEvent.setup();
		createVariant.mockResolvedValue({ data: { entity: savedVariant } });
		const { container } = await renderPage();

		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createVariant).toHaveBeenCalledTimes(1));
		expect(navigate).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:6239');
		});
		expect(container.querySelector('#synonyms')).toHaveValue('var-1');
	});

	it('Creates another variant from the kept values, without the saved ids', async () => {
		const user = userEvent.setup();
		createVariant.mockResolvedValue({ data: { entity: savedVariant } });
		await renderPage();

		await user.click(button('Save & Add Another'));
		await screen.findByText('Variant AGRKB:103000000000001 created');

		await user.click(button('Save & Add Another'));
		await waitFor(() => expect(createVariant).toHaveBeenCalledTimes(2));

		const payload = createVariant.mock.calls[1][0];
		expect(payload).not.toHaveProperty('id');
		expect(payload).not.toHaveProperty('curie');
		expect(payload).not.toHaveProperty('dateCreated');
		expect(payload.type).toEqual('Variant');
		expect(payload.taxon.curie).toEqual('NCBITaxon:6239');
		expect(payload.synonyms).toEqual(['var-1']);
		expect(payload.references.map((reference) => reference.curie)).toEqual(['AGRKB:101000000000041']);
		expect(payload.relatedNotes).toHaveLength(1);
		expect(payload.relatedNotes[0].freeText).toEqual('a note');
		expect(payload.relatedNotes[0]).not.toHaveProperty('id');
		expect(payload.relatedNotes[0]).not.toHaveProperty('dateCreated');
	});

	it('Clears the kept values', async () => {
		const user = userEvent.setup();
		createVariant.mockResolvedValue({ data: { entity: savedVariant } });
		const { container } = await renderPage();

		await user.click(button('Save & Add Another'));
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:6239');
		});
		expect(screen.getByText('a note')).toBeInTheDocument();

		// The shown notes and references tables carry column filter buttons also named Clear.
		await user.click(screen.getByText('Clear').closest('button'));

		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('');
		});
		expect(container.querySelector('#synonyms')).toHaveValue('');
		expect(screen.queryByText('a note')).not.toBeInTheDocument();
	});

	it('Names the new variant in the success message', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Add Another'));

		expect(await screen.findByText('Variant AGRKB:103000000000001 created')).toBeInTheDocument();
	});

	it('Returns to the variants table on cancel', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Cancel'));

		expect(navigate).toHaveBeenCalledWith('/variants');
	});
});
