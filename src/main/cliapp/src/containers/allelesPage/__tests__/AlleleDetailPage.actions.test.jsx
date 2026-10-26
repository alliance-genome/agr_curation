import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { alleleDetailData } from '../mockData/mockData.js';
import { FORM_LOAD_WAIT, markAlleleExtinct, renderAtAlleleRoute } from './alleleTestUtils';

const getAllele = vi.fn();
const deleteAllele = vi.fn();
const saveAlleleDetail = vi.fn();
const navigate = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele, deleteAllele, saveAlleleDetail })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService()
);

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

const LOADED_CURIE = 'AGRKB:101000000000009';
const loadedAllele = { ...alleleDetailData.entity, id: 9, curie: LOADED_CURIE };

const renderLoadedPage = async () => {
	renderAtAlleleRoute(<AlleleDetailPage />);

	await waitFor(() => expect(getAllele).toHaveBeenCalledWith('MGI:5146840'), FORM_LOAD_WAIT);
	await waitFor(
		() =>
			expect(screen.getAllByDisplayValue(alleleDetailData.entity.alleleSymbol.displayText).length).toBeGreaterThan(0),
		FORM_LOAD_WAIT
	);
};

const dialog = () => screen.getByRole('dialog');

// every test here loads and renders the whole detail form before acting on it
describe('<AlleleDetailPage /> header actions', { timeout: 30000 }, () => {
	beforeEach(() => {
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: loadedAllele } });
		deleteAllele.mockReset();
		deleteAllele.mockResolvedValue({ isSuccess: true, isError: false });
		saveAlleleDetail.mockReset();
		saveAlleleDetail.mockImplementation((allele) => Promise.resolve({ data: { entity: allele } }));
		navigate.mockReset();
		window.localStorage.removeItem('AlleleDetailFormSettings');
	});

	it('Opens the create page for a copy by the loaded allele curie', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);

		await renderLoadedPage();
		await user.click(screen.getByRole('button', { name: /Clone/i }));

		expect(open).toHaveBeenCalledWith(`/allele/create?from=${encodeURIComponent(LOADED_CURIE)}`, '_blank');
		open.mockRestore();
	});

	it('Deletes the allele once confirmed and returns to the alleles table', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('checkbox'));
		await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/alleles'), FORM_LOAD_WAIT);
		expect(deleteAllele.mock.calls[0][0].curie).toEqual(LOADED_CURIE);
	});

	it('Stays on the allele and says why when the deletion is refused', async () => {
		const user = userEvent.setup();
		deleteAllele.mockResolvedValue({
			isSuccess: false,
			isError: true,
			message: 'Allele is in use and cannot be deleted',
		});
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('checkbox'));
		await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));

		await waitFor(
			() => expect(screen.getByText('Allele is in use and cannot be deleted')).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Deprecates the allele by saving it as obsolete', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(saveAlleleDetail).toHaveBeenCalled(), FORM_LOAD_WAIT);
		const saved = saveAlleleDetail.mock.calls[0][0];
		expect(saved.obsolete).toBe(true);
		expect(saved.curie).toEqual(LOADED_CURIE);
		expect(deleteAllele).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Covers the page with the plain saving overlay while the deprecation is written', async () => {
		const user = userEvent.setup();
		let finishDeprecation;
		await renderLoadedPage();
		saveAlleleDetail.mockReturnValue(
			new Promise((resolve) => {
				finishDeprecation = () => resolve({ data: { entity: { ...loadedAllele, obsolete: true } } });
			})
		);

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		expect(await screen.findByText('Saving in progress...')).toBeInTheDocument();
		expect(screen.queryByRole('status', { name: 'Save progress' })).not.toBeInTheDocument();

		finishDeprecation();
		await waitFor(() => expect(screen.queryByText('Saving in progress...')).not.toBeInTheDocument(), FORM_LOAD_WAIT);
	});

	it('Deprecates the allele as last saved, leaving unsaved edits on the form', async () => {
		const user = userEvent.setup();
		const savedSymbol = alleleDetailData.entity.alleleSymbol.displayText;
		// a format text unlike the display text, so the display text input is the only one showing it
		getAllele.mockResolvedValue({
			data: {
				entity: { ...loadedAllele, alleleSymbol: { ...loadedAllele.alleleSymbol, formatText: 'saved-format-text' } },
			},
		});
		await renderLoadedPage();

		const symbolInput = screen.getByDisplayValue(savedSymbol);
		await user.clear(symbolInput);
		await user.type(symbolInput, 'unsaved-symbol');
		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(screen.getByText('Allele Deprecated')).toBeInTheDocument(), FORM_LOAD_WAIT);
		const deprecated = saveAlleleDetail.mock.calls[0][0];
		expect(deprecated.obsolete).toBe(true);
		expect(deprecated.alleleSymbol.displayText).toEqual(savedSymbol);

		// the edit is still on the form, now alongside the obsolete flag
		await user.click(screen.getByRole('button', { name: 'Save' }));
		await waitFor(() => expect(saveAlleleDetail).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		const saved = saveAlleleDetail.mock.calls[1][0];
		expect(saved.obsolete).toBe(true);
		expect(saved.alleleSymbol.displayText).toEqual('unsaved-symbol');
	});

	it('Leaves the form as it was when the deprecation is refused', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValueOnce({ response: { data: { errorMessage: 'Allele could not be saved' } } });
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(screen.getByText('Allele not deprecated:')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.getByText('Allele could not be saved')).toBeInTheDocument();
		expect(screen.queryByText('Allele Deprecated')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();

		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));
		await waitFor(() => expect(saveAlleleDetail).toHaveBeenCalledTimes(2), FORM_LOAD_WAIT);
		expect(saveAlleleDetail.mock.calls[1][0].obsolete).toBe(false);
	});
});
