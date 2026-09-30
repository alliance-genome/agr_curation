import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';

const createAllele = vi.fn();
const saveAlleleDetail = vi.fn();
const navigate = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();
const validate = vi.fn();
const search = vi.fn();

const PMID = { id: 9, prefix: 'PMID', name: 'PubMed', resourcePages: [{ id: 1, name: 'default' }] };

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
		search = search;
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
		replaceCrossReferencesForAllele = replaceCrossReferencesForAllele;
	},
}));

vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
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

// Other sections of the page have their own editors, so a cross reference's are found in its row,
// reached from its descriptor cell.
const crossReferenceRow = () => screen.getByLabelText('resourceDescriptor').closest('tr');

// The table reads every descriptor when it first shows a row, so a curie's can be filled in from then on.
const waitForDescriptors = () =>
	waitFor(() => expect(search.mock.calls.some(([endpoint]) => endpoint === Endpoints.Resource.DESCRIPTOR)).toBe(true));

// Leaving the curie cell sets the descriptor its prefix names and that descriptor's default page, so the
// row is complete once the curie and display name are typed.
const addCompleteCrossReference = async (user) => {
	await user.click(button('Add Cross Reference'));
	await waitForDescriptors();
	await user.type(crossReferenceRow().querySelector('#referencedCurie'), 'PMID:123');
	await user.type(crossReferenceRow().querySelector('#displayName'), 'PMID:123');
};

describe('<AlleleCreatePage /> cross references', () => {
	beforeEach(() => {
		createAllele.mockReset();
		createAllele.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000001' } } });
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockResolvedValue({ data: { entity: { id: 4242, curie: 'AGRKB:101000000000001' } } });
		navigate.mockReset();
		replaceCrossReferencesForAllele.mockReset();
		replaceCrossReferencesForAllele.mockResolvedValue({ data: { entities: [] } });
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
		search.mockReset();
		search.mockResolvedValue({ results: [PMID], totalResults: 1 });
		window.localStorage.removeItem('AlleleCreateFormSettings');
	});

	// Cross references are written through their own sub-resource, so creating an allele that has
	// them is two calls, and the second needs the id the first returns.
	it('Saves cross references against the allele it just created', async () => {
		const user = userEvent.setup();
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		expect(replaceCrossReferencesForAllele.mock.calls[0][0]).toBe(4242);
		expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001');
		expect(validate).not.toHaveBeenCalled();
	});

	it('Makes no second call when there are no cross references', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(navigate).toHaveBeenCalled());
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
		expect(validate).not.toHaveBeenCalled();
	});

	// The obvious next move after the cross references are rejected is to fix them and save again. That
	// must retry against the allele that already exists, not mint a second one.
	it('Does not create a second allele when saving again after a cross reference failure', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});

		await renderPage();

		await addCompleteCrossReference(user);
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

		await addCompleteCrossReference(user);
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

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		expect(navigate).not.toHaveBeenCalled();
		expect(await screen.findByText(/Could not update CrossReferences/)).toBeInTheDocument();
	});

	// The check runs in the browser before anything is written, so no allele is created either.
	it('Creates no allele while a cross reference is missing a required field', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Add Cross Reference'));
		await user.click(button('Save & Close'));

		expect(await screen.findByText('Some cross references are missing required fields')).toBeInTheDocument();
		expect(screen.getByText('Allele not saved:')).toBeInTheDocument();
		expect(within(crossReferenceRow()).getAllByText('Required field is empty')).toHaveLength(4);
		expect(createAllele).not.toHaveBeenCalled();
	});

	// A curie typed as just its prefix fills the descriptor but has no identifier.
	it('Creates no allele while a cross reference curie holds only its prefix', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Add Cross Reference'));
		await waitForDescriptors();
		await user.type(crossReferenceRow().querySelector('#referencedCurie'), 'PMID:');
		await user.type(crossReferenceRow().querySelector('#displayName'), 'PMID');
		await user.click(button('Save & Close'));

		expect(await screen.findByText('Identifier after the prefix is missing')).toBeInTheDocument();
		expect(createAllele).not.toHaveBeenCalled();
	});

	// Clicking Save takes focus from the curie cell, and the descriptor it names is set before the save's
	// required check reads the row, so a curie typed last still saves.
	it('Fills the descriptor from a curie typed last, in time for the save', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Add Cross Reference'));
		await waitForDescriptors();
		await user.type(crossReferenceRow().querySelector('#displayName'), 'PMID:123');
		await user.type(crossReferenceRow().querySelector('#referencedCurie'), 'PMID:123');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		const [sentRow] = replaceCrossReferencesForAllele.mock.calls[0][1];
		expect(sentRow.resourceDescriptorPage).toEqual({ id: 1, name: 'default' });
		expect(sentRow.referencedCurie).toBe('PMID:123');
	});
});
