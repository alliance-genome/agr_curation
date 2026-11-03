import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import { alleleDetailData } from '../mockData/mockData.js';
import { FORM_LOAD_WAIT, renderAtAlleleRoute } from './alleleTestUtils';

const getAllele = vi.fn();

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele })
);
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());
vi.mock('../../../service/AlleleSubResourceService', async () =>
	(await import('./alleleServiceMocks')).mockSubResourceService()
);

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

const AGRKB_CURIE = 'AGRKB:101000000000001';

/**
 * Renders the detail page for the given allele.
 *
 * @param {Object} allele the allele the page loads
 */
const renderPageFor = (allele) => {
	getAllele.mockResolvedValue({ data: { entity: allele } });
	renderAtAlleleRoute(<AlleleDetailPage />);
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
