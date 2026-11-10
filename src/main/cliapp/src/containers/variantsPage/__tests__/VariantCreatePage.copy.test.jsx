import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createVariant = vi.fn();
const getVariant = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/VariantService', () => ({
	VariantService: class {
		createVariant = createVariant;
		getVariant = getVariant;
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

const FORM_LOAD_WAIT = { timeout: 5000 };

const SOURCE_CURIE = 'AGRKB:103000000000001';

const sourceVariant = {
	id: 4242,
	curie: SOURCE_CURIE,
	primaryExternalId: 'WB:WBVar00000001',
	type: 'Variant',
	taxon: { id: 9, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	variantType: { id: 5, curie: 'SO:1000002', name: 'substitution' },
	dataProvider: { abbreviation: 'WB' },
	dateCreated: '2024-01-01T00:00:00Z',
	internal: false,
	obsolete: true,
	relatedNotes: [{ id: 31, freeText: 'a note', noteType: { name: 'comment' }, internal: false }],
	references: [{ id: 41, curie: 'AGRKB:101000000000041' }],
	synonyms: ['var-1'],
};

const renderPage = (entry = `/variant/create?from=${encodeURIComponent(SOURCE_CURIE)}`) =>
	renderWithClient(
		<MemoryRouter initialEntries={[entry]}>
			<VariantCreatePage />
		</MemoryRouter>
	);

const taxonInput = (container) => container.querySelector('input[name="taxon-input"]');
const button = (name) => screen.getByRole('button', { name });

describe('<VariantCreatePage /> cloning a variant', () => {
	beforeEach(() => {
		createVariant.mockReset();
		createVariant.mockResolvedValue({ data: { entity: { id: 5000, curie: 'AGRKB:103000000000002' } } });
		getVariant.mockReset();
		getVariant.mockResolvedValue({ data: { entity: sourceVariant } });
		navigate.mockReset();
		window.localStorage.removeItem('VariantCreateFormSettings');
	});

	it('Starts from the variant named in the URL', async () => {
		const { container } = await renderPage();

		await waitFor(() => expect(taxonInput(container)).toHaveValue('NCBITaxon:6239'), FORM_LOAD_WAIT);
		expect(getVariant).toHaveBeenCalledWith(SOURCE_CURIE);
		expect(container.querySelector('input[name="variantType-input"]')).toHaveValue('SO:1000002');
	});

	it('Does not load a source when none is named', async () => {
		await renderPage('/variant/create');

		expect(screen.getByRole('heading', { name: 'Add Variant' })).toBeInTheDocument();
		expect(getVariant).not.toHaveBeenCalled();
	});

	it('Says the copy is loading while the variant is fetched', async () => {
		let resolveVariant;
		getVariant.mockReturnValue(new Promise((resolve) => (resolveVariant = resolve)));
		const { container } = await renderPage();

		await waitFor(
			() => expect(screen.getByRole('heading', { name: 'Loading variant to clone...' })).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);

		resolveVariant({ data: { entity: sourceVariant } });
		await waitFor(() => expect(taxonInput(container)).toHaveValue('NCBITaxon:6239'), FORM_LOAD_WAIT);
		expect(screen.queryByText('Loading variant to clone...')).not.toBeInTheDocument();
	});

	it('Reports a source variant that cannot be loaded and leaves the form blank', async () => {
		getVariant.mockRejectedValue(new Error('not found'));
		const { container } = await renderPage();

		expect(
			await screen.findByText(`Could not load variant ${SOURCE_CURIE} to clone`, {}, FORM_LOAD_WAIT)
		).toBeInTheDocument();
		expect(taxonInput(container)).toHaveValue('');
	});

	it('Creates a new variant rather than sending the stored one back', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await waitFor(() => expect(taxonInput(container)).toHaveValue('NCBITaxon:6239'), FORM_LOAD_WAIT);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createVariant).toHaveBeenCalled(), FORM_LOAD_WAIT);
		const payload = createVariant.mock.calls[0][0];
		['id', 'curie', 'primaryExternalId', 'dataProvider', 'dateCreated'].forEach((field) =>
			expect(payload).not.toHaveProperty(field)
		);
		expect(payload.obsolete).toBe(false);
		expect(payload.taxon.curie).toEqual('NCBITaxon:6239');
		expect(payload.variantType.curie).toEqual('SO:1000002');
		expect(payload.synonyms).toEqual(['var-1']);
		expect(payload.references[0].curie).toEqual('AGRKB:101000000000041');
		expect(payload.relatedNotes[0]).not.toHaveProperty('id');
		expect(payload.relatedNotes[0].freeText).toEqual('a note');
	});
});
