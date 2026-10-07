import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';

const getAGM = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AffectedGenomicModelService', () => ({
	AffectedGenomicModelService: class {
		getAGM = getAGM;
		saveAGM = vi.fn();
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const AffectedGenomicModelDetailPage = (await import('../AffectedGenomicModelDetailPage')).default;

// each wait follows a stubbed call through a full render of the form, which can outlast the default under load
const FORM_LOAD_WAIT = { timeout: 5000 };

const AGRKB_CURIE = 'AGRKB:104000000000001';

const agm = {
	id: 3,
	curie: AGRKB_CURIE,
	primaryExternalId: 'ZFIN:ZDB-FISH-150901-1',
	agmFullName: { displayText: 'AB', formatText: 'AB' },
};

/**
 * Renders the detail page for the given AGM.
 *
 * @param {Object} agmEntity the AGM the page loads
 */
const renderPageFor = (agmEntity) => {
	getAGM.mockResolvedValue({ data: { entity: agmEntity } });
	renderWithClient(
		<MemoryRouter initialEntries={['/agm/ZFIN:ZDB-FISH-150901-1']}>
			<Routes>
				<Route path="/agm/:identifier" element={<AffectedGenomicModelDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<AffectedGenomicModelDetailPage /> header', () => {
	beforeEach(() => {
		getAGM.mockReset();
	});

	it('Shows the full name and curie when there is no primary external ID', async () => {
		renderPageFor({ ...agm, primaryExternalId: undefined });

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(`AGM: AB (${AGRKB_CURIE})`),
			FORM_LOAD_WAIT
		);
	});
});
