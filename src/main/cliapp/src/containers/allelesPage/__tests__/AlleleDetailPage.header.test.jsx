import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
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

const AGRKB_CURIE = 'AGRKB:101000000000001';

/**
 * Renders the detail page for the given allele.
 *
 * @param {Object} allele the allele the page loads
 */
const renderPageFor = (allele) => {
	getAllele.mockResolvedValue({ data: { entity: allele } });
	renderWithClient(
		<MemoryRouter initialEntries={['/allele/MGI:5146840']}>
			<Routes>
				<Route path="/allele/:identifier" element={<AlleleDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<AlleleDetailPage /> header', () => {
	beforeEach(() => {
		getAllele.mockReset();
	});

	it('Shows the symbol and curie when there is no primary external ID', async () => {
		renderPageFor({ ...alleleDetailData.entity, primaryExternalId: undefined, curie: AGRKB_CURIE });

		await waitFor(
			() =>
				expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
					`Allele: Ahdc1Gt(IST11463A1)Tigm (${AGRKB_CURIE})`
				),
			FORM_LOAD_WAIT
		);
	});
});
