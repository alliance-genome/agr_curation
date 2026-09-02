import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
	},
}));

// Typing into an autocomplete fires PrimeReact's debounced completeMethod, which builds its own
// SearchService. Left unstubbed it reaches ApiClient after the test has finished and throws in a
// timer, failing whichever test happens to run next.
vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
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

	it('Sends an empty form to the API and shows the messages it returns', async () => {
		const user = userEvent.setup();
		// Symbol and taxon are validated server side, so an empty form is posted rather than
		// stopped, and the response supplies the messages.
		createAllele.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: {
					errorMessage: 'Could not create Allele',
					errorMessages: { alleleSymbol: 'Required field is empty', taxon: 'Required field is empty' },
					supplementalData: { errorMap: { alleleSymbol: 'Required field is empty' } },
				},
			},
		});

		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(createAllele).toHaveBeenCalled());
		await waitFor(() => {
			expect(screen.getAllByText('Required field is empty').length).toBeGreaterThan(0);
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
		// the seeded symbol is part of a fresh form, so the reset leaves it in place
		expect(screen.getByRole('columnheader', { name: 'Display Text' })).toBeInTheDocument();
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
		expect(screen.getByRole('columnheader', { name: 'Display Text' })).toBeInTheDocument();
	});

	it('Opens with an empty symbol and no button to add one', async () => {
		await renderPage();

		expect(screen.getByRole('columnheader', { name: 'Display Text' })).toBeInTheDocument();
		// every allele carries exactly one symbol, so it is seeded rather than added
		expect(screen.queryByRole('button', { name: 'Add Symbol' })).not.toBeInTheDocument();
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
