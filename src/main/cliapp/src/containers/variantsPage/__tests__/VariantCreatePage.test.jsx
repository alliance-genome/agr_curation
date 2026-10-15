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

	it('Stays on a blank form on save and add another', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		const taxon = container.querySelector('input[name="taxon-input"]');
		await user.type(taxon, 'NCBITaxon:6239');
		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createVariant).toHaveBeenCalled());

		expect(navigate).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('');
		});
	});

	it('Returns to the variants table on cancel', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Cancel'));

		expect(navigate).toHaveBeenCalledWith('/variants');
	});
});
