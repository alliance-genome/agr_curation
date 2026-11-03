import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../../cassettesPage/mockData/mockData.js';

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

const construct = {
	id: 7,
	curie: 'AGRKB:102000000000001',
	primaryExternalId: 'FB:FBtp0000001',
	constructSymbol: { displayText: 'P{KK102259}', formatText: 'P{KK102259}' },
	references: [],
	constructComponents: [{ id: 1, componentSymbol: 'croc', relation: { name: 'expresses' } }],
	constructGenomicEntityAssociations: [],
	constructCassetteAssociations: [
		{ id: 3, relation: { name: 'has_transcriptional_unit' }, constructCassetteAssociationObject: { id: 200133829 } },
	],
};

/**
 * Renders the detail page for the given construct.
 *
 * @param {Object} constructEntity the construct the page loads
 */
const renderPageFor = (constructEntity) => {
	getConstruct.mockResolvedValue({ data: { entity: constructEntity } });
	renderWithClient(
		<MemoryRouter initialEntries={['/construct/FB:FBtp0000001']}>
			<Routes>
				<Route path="/construct/:identifier" element={<ConstructDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<ConstructDetailPage /> cassettes', () => {
	beforeEach(() => {
		getConstruct.mockReset();
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Shows the Cassettes table instead of the component tables', async () => {
		renderPageFor(construct);

		await waitFor(() => expect(screen.getByRole('heading', { name: 'Cassettes' })).toBeInTheDocument(), PAGE_LOAD_WAIT);
		expect(await screen.findByText(/Construct Cassettes Table/i)).toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Free Text Components' })).not.toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Component Associations' })).not.toBeInTheDocument();
	});

	it('Shows no table when the construct has no cassettes', async () => {
		renderPageFor({ ...construct, constructCassetteAssociations: [] });

		await waitFor(() => expect(screen.getByRole('heading', { name: 'Cassettes' })).toBeInTheDocument(), PAGE_LOAD_WAIT);
		expect(screen.queryByText(/Construct Cassettes Table/i)).not.toBeInTheDocument();
	});
});
