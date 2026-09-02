import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the service directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleCreatePage = (await import('../AlleleCreatePage')).default;

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AlleleCreatePage />
		</BrowserRouter>
	);

const heading = (name) => screen.queryByRole('heading', { name });
const button = (name) => screen.getByRole('button', { name });

describe('<AlleleCreatePage />', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { curie: 'AGRKB:101000000000001' } } });
		navigate.mockReset();
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	it('Renders the create form rather than the detail fields', async () => {
		await renderPage();

		expect(heading('Add Allele')).toBeInTheDocument();
		expect(heading('Taxon')).toBeInTheDocument();
		// no identifier is curated here; the API mints the curie
		expect(screen.queryByLabelText('primaryExternalId')).not.toBeInTheDocument();
		expect(heading('Curie')).not.toBeInTheDocument();
		expect(heading('Date Created')).not.toBeInTheDocument();
	});

	it('Offers all four actions', async () => {
		await renderPage();

		expect(button('Clear')).toBeInTheDocument();
		expect(button('Cancel')).toBeInTheDocument();
		expect(button('Save & Close')).toBeInTheDocument();
		expect(button('Save & Add Another')).toBeInTheDocument();
	});

	it('Blocks a save with no taxon and sends no request', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => {
			expect(screen.getAllByText('Required').length).toBeGreaterThan(0);
		});
		expect(createAllele).not.toHaveBeenCalled();
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

	it('Posts a payload without the blank placeholders and opens the new allele', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.type(container.querySelector('input[name="taxon-input"]'), 'NCBITaxon:6239');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());

		const payload = createAllele.mock.calls[0][0];
		expect(payload.type).toEqual('Allele');
		expect(payload).not.toHaveProperty('primaryExternalId');
		expect(payload.taxon).toEqual({ curie: 'NCBITaxon:6239' });
		// the blank inCollection the initial state carries would be rejected by the API
		expect(payload).not.toHaveProperty('inCollection');

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'));
	});

	it('Stays on a blank form on save and add another', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		const taxon = container.querySelector('input[name="taxon-input"]');
		await user.type(taxon, 'NCBITaxon:6239');
		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());

		expect(navigate).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('');
		});
	});

	it('Adds a symbol', async () => {
		const user = userEvent.setup();
		await renderPage();

		const addSymbol = button('Add Symbol');
		expect(addSymbol).toBeEnabled();

		await user.click(addSymbol);

		await waitFor(() => {
			expect(screen.getByRole('columnheader', { name: 'Display Text' })).toBeInTheDocument();
		});
		// an allele carries a single symbol, so adding a second is not offered
		expect(button('Add Symbol')).toBeDisabled();
	});

	it('Shows a nested slot annotation error beside the field it belongs to', async () => {
		const user = userEvent.setup();
		// the shape AlleleValidator produces: a flat message plus the per field detail under
		// supplementalData.errorMap, keyed by the entity rather than a row index
		createAllele.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not create Allele',
					errorMessages: { alleleSymbol: 'nameType - Required' },
					supplementalData: { errorMap: { alleleSymbol: { nameType: 'Required' } } },
				},
			},
		});

		const { container } = await renderPage();

		await user.click(button('Add Symbol'));
		await user.type(container.querySelector('input[name="taxon-input"]'), 'NCBITaxon:6239');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());
		await waitFor(() => {
			expect(screen.getByText('Required')).toBeInTheDocument();
		});
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Returns to the alleles table on cancel', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Cancel'));

		expect(navigate).toHaveBeenCalledWith('/alleles');
	});
});
