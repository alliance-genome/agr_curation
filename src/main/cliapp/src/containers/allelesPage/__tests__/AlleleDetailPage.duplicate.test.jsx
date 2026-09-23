import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { alleleDetailData } from '../mockData/mockData.js';

const getAllele = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		getAllele = getAllele;
		saveAlleleDetail = vi.fn();
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

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const LOADED_CURIE = 'AGRKB:101000000000009';

describe('<AlleleDetailPage /> duplicating the allele', () => {
	beforeEach(() => {
		getAllele.mockReset();
		getAllele.mockResolvedValue({ data: { entity: { ...alleleDetailData.entity, id: 9, curie: LOADED_CURIE } } });
		window.localStorage.removeItem('AlleleDetailFormSettings');
	});

	it('Opens the create page for a copy by the loaded allele curie', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);

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
		await user.click(screen.getByRole('button', { name: /Duplicate/i }));

		expect(open).toHaveBeenCalledWith(`/allele/create?from=${encodeURIComponent(LOADED_CURIE)}`, '_blank');
		open.mockRestore();
	});
});
