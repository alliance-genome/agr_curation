import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';

const getVariant = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/VariantService', () => ({
	VariantService: class {
		getVariant = getVariant;
		saveVariant = vi.fn();
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const VariantDetailPage = (await import('../VariantDetailPage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const AGRKB_CURIE = 'AGRKB:103000000000001';

const variant = {
	id: 5,
	curie: AGRKB_CURIE,
	primaryExternalId: 'WB:WBVar00000001',
	variantType: { curie: 'SO:0000694', name: 'SNP' },
	references: [],
};

/**
 * Renders the detail page for the given variant.
 *
 * @param {Object} variantEntity the variant the page loads
 */
const renderPageFor = (variantEntity) => {
	getVariant.mockResolvedValue({ data: { entity: variantEntity } });
	renderWithClient(
		<MemoryRouter initialEntries={['/variant/WB:WBVar00000001']}>
			<Routes>
				<Route path="/variant/:identifier" element={<VariantDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<VariantDetailPage /> header', () => {
	beforeEach(() => {
		getVariant.mockReset();
	});

	it('Shows the variant type and curie when there is no primary external ID', async () => {
		renderPageFor({ ...variant, primaryExternalId: undefined });

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(`Variant: SNP (${AGRKB_CURIE})`),
			FORM_LOAD_WAIT
		);
	});
});
