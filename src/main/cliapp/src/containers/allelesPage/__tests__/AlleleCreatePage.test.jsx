import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const navigate = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();

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
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
		replaceCrossReferencesForAllele = replaceCrossReferencesForAllele;
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
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000001' } } });
		navigate.mockReset();
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
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
	});

	it('Blanks the symbol editors on clear', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.type(container.querySelector('#displayText'), 'abc-1');
		expect(container.querySelector('#displayText')).toHaveValue('abc-1');

		await user.click(button('Clear'));

		// The row stays mounted across a reset, so its editors only blank if the row remounts.
		await waitFor(() => {
			expect(container.querySelector('#displayText')).toHaveValue('');
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
		// a fresh form still carries an empty symbol, ready to edit
		expect(container.querySelector('#displayText')).toHaveValue('');
	});

	it('Opens with an empty symbol and no button to add one', async () => {
		const { container } = await renderPage();

		expect(screen.getByRole('columnheader', { name: 'Display Text' })).toBeInTheDocument();
		expect(container.querySelector('#displayText')).toHaveValue('');
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

	// Cross references are written through their own sub-resource, so creating an allele that has
	// them is two calls, and the second needs the id the first returns.
	it('Saves cross references against the allele it just created', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Add Cross Reference'));
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		expect(replaceCrossReferencesForAllele.mock.calls[0][0]).toBe(4242);
		expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001');
	});

	it('Makes no second call when there are no cross references', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(navigate).toHaveBeenCalled());
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
	});

	// The obvious next move after the cross references are rejected is to fix them and save again. That
	// must retry against the allele that already exists, not mint a second one.
	it('Does not create a second allele when saving again after a cross reference failure', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});

		await renderPage();

		await user.click(button('Add Cross Reference'));
		await user.click(button('Save & Close'));
		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1));

		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(2));
		expect(createAllele).toHaveBeenCalledTimes(1);
		expect(replaceCrossReferencesForAllele.mock.calls[1][0]).toBe(4242);
		expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001');
	});

	// The allele exists by then, so the work is recoverable from its detail page. Navigating away
	// would strand the curator's cross references with no way back to them.
	it('Keeps the curator on the page when the cross references fail to save', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});

		await renderPage();

		await user.click(button('Add Cross Reference'));
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		expect(navigate).not.toHaveBeenCalled();
		expect(await screen.findByText(/Could not update CrossReferences/)).toBeInTheDocument();
	});
});
