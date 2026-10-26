import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Endpoints } from '../../../constants/Endpoints';
import { button, NAVIGATION_WAIT, OUTSIDE_SAVE_PROGRESS, renderInRouter } from './alleleTestUtils';

const createAllele = vi.fn();
const saveAlleleDetail = vi.fn();
const navigate = vi.fn();
const replaceCrossReferencesForAllele = vi.fn();
const validate = vi.fn();
const search = vi.fn();

const PMID = { id: 9, prefix: 'PMID', name: 'PubMed', resourcePages: [{ id: 1, name: 'default' }] };

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ createAllele, saveAlleleDetail })
);
vi.mock('../../../service/SearchService', async () =>
	(await import('./alleleServiceMocks')).mockSearchService({ search })
);
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService({
		crossreferences: { replace: replaceCrossReferencesForAllele },
	})
);

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

const renderPage = () => renderInRouter(<AlleleCreatePage />);

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
	await user.type(crossReferenceRow().querySelector('[aria-label="referencedCurie"]'), 'PMID:123');
	await user.type(crossReferenceRow().querySelector('[aria-label="displayName"]'), 'PMID:123');
};

describe('<AlleleCreatePage /> cross references', { timeout: 30000 }, () => {
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
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'), NAVIGATION_WAIT);
		expect(validate).not.toHaveBeenCalled();
	});

	it('Shows the save in progress until the cross references are written', async () => {
		const user = userEvent.setup();
		let finishWrite;
		replaceCrossReferencesForAllele.mockReturnValue(new Promise((resolve) => (finishWrite = resolve)));
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		const progress = screen.getByRole('status', { name: 'Save progress' });
		expect(within(progress).getByText(/^Allele:/)).toHaveTextContent('Allele: Saved');
		expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Saving');
		expect(navigate).not.toHaveBeenCalled();

		finishWrite({ data: { entities: [] } });
		await waitFor(() =>
			expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Saved')
		);
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'), NAVIGATION_WAIT);
	});

	it('Makes no second call when there are no cross references', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(navigate).toHaveBeenCalled(), NAVIGATION_WAIT);
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
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'), NAVIGATION_WAIT);
	});

	it('Offers no second save while it shows everything saved before moving on', async () => {
		const user = userEvent.setup();
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));
		const progress = await screen.findByRole('status', { name: 'Save progress' });
		await waitFor(() => expect(within(progress).getByRole('heading', { level: 1 })).toHaveTextContent('Saved'));

		expect(navigate).not.toHaveBeenCalled();
		expect(button('Save & Close')).toBeDisabled();
		expect(button('Save & Add Another')).toBeDisabled();
		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/allele/AGRKB:101000000000001'), NAVIGATION_WAIT);
		expect(createAllele).toHaveBeenCalledTimes(1);
		expect(saveAlleleDetail).not.toHaveBeenCalled();
	});

	it('Lists the allele update and the retried rows when saving again after a failure', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));
		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalledTimes(1));
		await user.click(button('Save & Close'));

		const progress = await screen.findByRole('status', { name: 'Save progress' });
		await waitFor(() =>
			expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Saved')
		);
		expect(within(progress).getByText(/^Allele:/)).toHaveTextContent('Allele: Saved');
		expect(saveAlleleDetail).toHaveBeenCalledTimes(1);
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
		expect(await screen.findByText(/Could not update CrossReferences/, OUTSIDE_SAVE_PROGRESS)).toBeInTheDocument();
	});

	it('Shows the cross references as not saved when the allele is not created', async () => {
		const user = userEvent.setup();
		createAllele.mockRejectedValue({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not create Allele' } },
		});
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));

		const progress = await screen.findByRole('status', { name: 'Save progress' });
		await waitFor(() => expect(within(progress).getByText(/^Allele:/)).toHaveTextContent('Allele: Failed'));
		expect(within(progress).getByText('Could not create Allele')).toBeInTheDocument();
		expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Not saved');
		expect(within(progress).getByRole('heading', { level: 1 })).toHaveTextContent('Save finished with errors');
		expect(replaceCrossReferencesForAllele).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Shows the cross references failed after the allele was created', async () => {
		const user = userEvent.setup();
		replaceCrossReferencesForAllele.mockRejectedValue({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update CrossReferences' } },
		});
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Close'));

		const progress = await screen.findByRole('status', { name: 'Save progress' });
		await waitFor(() =>
			expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Failed')
		);
		expect(within(progress).getByText(/^Allele:/)).toHaveTextContent('Allele: Saved');
		expect(within(progress).getByText('Could not update CrossReferences')).toBeInTheDocument();
	});

	it('Shows everything saved on Save & Add Another and clears the form', async () => {
		const user = userEvent.setup();
		await renderPage();

		await addCompleteCrossReference(user);
		await user.click(button('Save & Add Another'));

		const progress = await screen.findByRole('status', { name: 'Save progress' });
		await waitFor(() =>
			expect(within(progress).getByText(/^Cross references:/)).toHaveTextContent('Cross references: Saved')
		);
		expect(within(progress).getByText(/^Allele:/)).toHaveTextContent('Allele: Saved');
		await waitFor(
			() => expect(screen.queryByRole('status', { name: 'Save progress' })).not.toBeInTheDocument(),
			NAVIGATION_WAIT
		);
		expect(screen.queryByLabelText('resourceDescriptor')).not.toBeInTheDocument();
		expect(navigate).not.toHaveBeenCalled();
	});

	// Clicking Save takes focus from the curie cell, and the descriptor it names is set before the save
	// reads the row, so a curie typed last is sent with its page.
	it('Fills the descriptor from a curie typed last, in time for the save', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Add Cross Reference'));
		await waitForDescriptors();
		await user.type(crossReferenceRow().querySelector('[aria-label="displayName"]'), 'PMID:123');
		await user.type(crossReferenceRow().querySelector('[aria-label="referencedCurie"]'), 'PMID:123');
		await user.click(button('Save & Close'));

		await waitFor(() => expect(replaceCrossReferencesForAllele).toHaveBeenCalled());
		const [sentRow] = replaceCrossReferencesForAllele.mock.calls[0][1];
		expect(sentRow.resourceDescriptorPage).toEqual({ id: 1, name: 'default' });
		expect(sentRow.referencedCurie).toBe('PMID:123');
	});
});
