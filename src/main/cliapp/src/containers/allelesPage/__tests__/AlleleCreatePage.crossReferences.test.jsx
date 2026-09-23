import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAllele = vi.fn();
const saveAlleleDetail = vi.fn();
const navigate = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		createAllele = createAllele;
		saveAlleleDetail = saveAlleleDetail;
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

const button = (name) => screen.getByRole('button', { name });

describe('<AlleleCreatePage /> cross references', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000001' } } });
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000001' } } });
		navigate.mockReset();
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		window.localStorage.removeItem('AlleleCreateFormSettings');
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

	// The form stays editable while the rows are being fixed, so saving again has to carry whatever was
	// changed in it. Retrying the cross references alone would drop those edits without saying so.
	it('Sends allele edits made while fixing a failed cross reference', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});

		await renderPage();

		await user.click(button('Add Cross Reference'));
		await user.click(button('Save & Close'));
		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1));

		await user.click(button('Save & Close'));

		await waitFor(() => expect(saveAlleleDetail).toHaveBeenCalledTimes(1));
		expect(saveAlleleDetail.mock.calls[0][0]).toMatchObject({ id: 4242 });
		expect(createAllele).toHaveBeenCalledTimes(1);
	}, 30000);

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
