import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

const getAllele = vi.fn();
const deleteAllele = vi.fn();
const saveAlleleDetail = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		getAllele = getAllele;
		deleteAllele = deleteAllele;
		saveAlleleDetail = saveAlleleDetail;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/CrossReferenceService', () => ({
	CrossReferenceService: class {
		getCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
		replaceCrossReferencesForAllele = vi.fn(() => Promise.resolve({ data: { entities: [] } }));
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const LOADED_CURIE = 'AGRKB:101000000000009';
const loadedAllele = { ...alleleDetailData.entity, id: 9, curie: LOADED_CURIE };

const renderLoadedPage = async () => {
	renderWithClient(
		<MemoryRouter initialEntries={['/allele/MGI:5146840']}>
			<Routes>
				<Route path="/allele/:identifier" element={<AlleleDetailPage />} />
			</Routes>
		</MemoryRouter>
	);

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
		await user.click(screen.getByRole('button', { name: /Duplicate/i }));

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
});
