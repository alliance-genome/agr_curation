import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const getVariant = vi.fn();
const saveVariant = vi.fn();
const deleteVariant = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/VariantService', () => ({
	VariantService: class {
		getVariant = getVariant;
		saveVariant = saveVariant;
		deleteVariant = deleteVariant;
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

const VariantDetailPage = (await import('../VariantDetailPage')).default;

const FORM_LOAD_WAIT = { timeout: 5000 };

const LOADED_CURIE = 'AGRKB:103000000000009';

const loadedVariant = {
	id: 9,
	curie: LOADED_CURIE,
	primaryExternalId: 'WB:WBVar00000001',
	taxon: { curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	variantType: { curie: 'SO:0000694', name: 'SNP' },
	internal: false,
	obsolete: false,
	references: [],
};

const renderLoadedPage = async () => {
	renderWithClient(
		<MemoryRouter initialEntries={['/variant/WB:WBVar00000001']}>
			<Routes>
				<Route path="/variant/:identifier" element={<VariantDetailPage />} />
			</Routes>
		</MemoryRouter>
	);

	await waitFor(() => expect(getVariant).toHaveBeenCalledWith('WB:WBVar00000001'), FORM_LOAD_WAIT);
	await waitFor(() => expect(screen.getByRole('button', { name: 'Delete' })).toBeEnabled(), FORM_LOAD_WAIT);
};

const dialog = () => screen.getByRole('dialog');

describe('<VariantDetailPage /> header actions', { timeout: 30000 }, () => {
	beforeEach(() => {
		getVariant.mockReset();
		getVariant.mockResolvedValue({ data: { entity: loadedVariant } });
		deleteVariant.mockReset();
		deleteVariant.mockResolvedValue({ isSuccess: true, isError: false });
		saveVariant.mockReset();
		saveVariant.mockImplementation((variant) => Promise.resolve({ data: { entity: variant } }));
		navigate.mockReset();
		window.localStorage.removeItem('VariantDetailFormSettings');
	});

	it('Opens the create page for a copy by the loaded variant curie', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);

		await renderLoadedPage();
		await user.click(screen.getByRole('button', { name: /Clone/i }));

		expect(open).toHaveBeenCalledWith(`/variant/create?from=${encodeURIComponent(LOADED_CURIE)}`, '_blank');
		open.mockRestore();
	});

	it('Deletes the variant once confirmed and returns to the variants table', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('checkbox'));
		await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/variants'), FORM_LOAD_WAIT);
		expect(deleteVariant.mock.calls[0][0].curie).toEqual(LOADED_CURIE);
	});

	it('Stays on the variant and says why when the deletion is refused', async () => {
		const user = userEvent.setup();
		deleteVariant.mockResolvedValue({
			isSuccess: false,
			isError: true,
			message: 'Variant is in use and cannot be deleted: Variant has allele association(s)',
		});
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('checkbox'));
		await user.click(within(dialog()).getByRole('button', { name: 'Delete' }));

		await waitFor(
			() =>
				expect(
					screen.getByText('Variant is in use and cannot be deleted: Variant has allele association(s)')
				).toBeInTheDocument(),
			FORM_LOAD_WAIT
		);
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Deprecates the variant by saving it as obsolete', async () => {
		const user = userEvent.setup();
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(screen.getByText('Variant Deprecated')).toBeInTheDocument(), FORM_LOAD_WAIT);
		const saved = saveVariant.mock.calls[0][0];
		expect(saved.obsolete).toBe(true);
		expect(saved.curie).toEqual(LOADED_CURIE);
		expect(deleteVariant).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});

	it('Reports a refused deprecation', async () => {
		const user = userEvent.setup();
		saveVariant.mockRejectedValueOnce({ response: { data: { errorMessage: 'Variant could not be saved' } } });
		await renderLoadedPage();

		await user.click(screen.getByRole('button', { name: 'Delete' }));
		await user.click(within(dialog()).getByRole('button', { name: 'Deprecate' }));

		await waitFor(() => expect(screen.getByText('Variant not deprecated:')).toBeInTheDocument(), FORM_LOAD_WAIT);
		expect(screen.getByText('Variant could not be saved')).toBeInTheDocument();
		expect(screen.queryByText('Variant Deprecated')).not.toBeInTheDocument();
	});
});
