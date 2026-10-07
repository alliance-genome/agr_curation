import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';

const getConstruct = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the service directly.
vi.mock('../../../service/ConstructService', () => ({
	ConstructService: class {
		getConstruct = getConstruct;
	},
}));

const ConstructDetailPage = (await import('../ConstructDetailPage')).default;

// each wait follows a stubbed call through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

const AGRKB_CURIE = 'AGRKB:102000000000001';

const construct = {
	id: 7,
	curie: AGRKB_CURIE,
	primaryExternalId: 'ZFIN:ZDB-TGCONSTRCT-070117-175',
	constructSymbol: {
		displayText: 'Tg(hsp70l:GFP)',
		formatText: 'Tg(hsp70l:GFP)',
	},
	references: [],
	constructGenomicEntityAssociations: [],
};

/**
 * Renders the detail page for the given construct.
 *
 * @param {Object} constructEntity the construct the page loads
 */
const renderPageFor = (constructEntity) => {
	getConstruct.mockResolvedValue({ data: { entity: constructEntity } });
	renderWithClient(
		<MemoryRouter initialEntries={['/construct/ZFIN:ZDB-TGCONSTRCT-070117-175']}>
			<Routes>
				<Route path="/construct/:identifier" element={<ConstructDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<ConstructDetailPage /> header', () => {
	beforeEach(() => {
		getConstruct.mockReset();
	});

	it('Shows the symbol and primary external ID', async () => {
		renderPageFor(construct);

		await waitFor(
			() =>
				expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
					'Construct: Tg(hsp70l:GFP) (ZFIN:ZDB-TGCONSTRCT-070117-175)'
				),
			PAGE_LOAD_WAIT
		);
	});

	it('Shows the symbol and curie when there is no primary external ID', async () => {
		renderPageFor({ ...construct, primaryExternalId: undefined });

		await waitFor(
			() =>
				expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(
					`Construct: Tg(hsp70l:GFP) (${AGRKB_CURIE})`
				),
			PAGE_LOAD_WAIT
		);
	});

	it('Shows the curie alone when there is no symbol or primary external ID', async () => {
		renderPageFor({ ...construct, primaryExternalId: undefined, constructSymbol: undefined });

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(`Construct: ${AGRKB_CURIE}`),
			PAGE_LOAD_WAIT
		);
	});
});
