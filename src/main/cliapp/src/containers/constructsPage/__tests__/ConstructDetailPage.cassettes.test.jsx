import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../../cassettesPage/mockData/mockData.js';

const { getConstruct, getRows, replaceRows } = vi.hoisted(() => ({
	getConstruct: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/ConstructService', async () =>
	(await import('./constructServiceMocks')).mockConstructService({ getConstruct })
);
vi.mock('../../../service/ConstructSubResourceService', async () =>
	(await import('./constructServiceMocks')).mockConstructSubResourceService({ getRows, replaceRows })
);

const ConstructDetailPage = (await import('../ConstructDetailPage')).default;

// each wait follows a stubbed call through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

const construct = {
	id: 7,
	curie: 'AGRKB:102000000000001',
	primaryExternalId: 'FB:FBtp0000001',
	constructSymbol: { displayText: 'P{KK102259}', formatText: 'P{KK102259}' },
};

const CASSETTE_ASSOCIATIONS = [
	{ id: 3, relation: { name: 'has_transcriptional_unit' }, constructCassetteAssociationObject: { id: 200133829 } },
];

/**
 * Renders the detail page for the construct, holding the given cassette associations.
 *
 * @param {Object[]} cassetteAssociations the associations the cassette associations sub-resource returns
 */
const renderPageWith = (cassetteAssociations) => {
	getConstruct.mockResolvedValue({ data: { entity: construct } });
	getRows.mockImplementation((path) =>
		Promise.resolve({ data: { entities: path === 'cassetteassociations' ? cassetteAssociations : [] } })
	);
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
		getRows.mockReset();
		replaceRows.mockReset();
		replaceRows.mockImplementation((path, constructId, rows) => Promise.resolve({ data: { entities: rows } }));
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Shows the Cassettes table instead of the component tables', async () => {
		renderPageWith(CASSETTE_ASSOCIATIONS);

		await waitFor(() => expect(screen.getByRole('heading', { name: 'Cassettes' })).toBeInTheDocument(), PAGE_LOAD_WAIT);
		expect(await screen.findByText(/Construct Cassettes Table/i)).toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Free Text Components' })).not.toBeInTheDocument();
		expect(screen.queryByRole('heading', { name: 'Component Associations' })).not.toBeInTheDocument();
	});

	it('Shows no table when the construct has no cassettes', async () => {
		renderPageWith([]);

		await waitFor(() => expect(screen.getByRole('heading', { name: 'Cassettes' })).toBeInTheDocument(), PAGE_LOAD_WAIT);
		expect(screen.queryByText(/Construct Cassettes Table/i)).not.toBeInTheDocument();
	});

	it('removes a cassette in the pop-up, keeping it pending until its own Save writes the list', async () => {
		const user = userEvent.setup();
		renderPageWith(CASSETTE_ASSOCIATIONS);

		const editButton = await screen.findByRole('button', { name: 'Edit Cassettes' }, PAGE_LOAD_WAIT);
		await waitFor(() => expect(getRows).toHaveBeenCalledWith('cassetteassociations', construct.id));
		await user.click(editButton);
		const dialog = await screen.findByRole('dialog');
		const removeButton = await waitFor(() => {
			const trash = dialog.querySelector('tbody .pi-trash');
			expect(trash).not.toBeNull();
			return trash.closest('button');
		});
		await user.click(removeButton);
		await user.click(within(dialog).getByRole('button', { name: 'Keep Edits' }));

		const cassettesSection = screen.getByRole('button', { name: 'Save Cassettes' }).closest('.grid');
		expect(within(cassettesSection).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();

		await user.click(screen.getByRole('button', { name: 'Save Cassettes' }));

		await waitFor(() => expect(replaceRows).toHaveBeenCalledWith('cassetteassociations', construct.id, []));
		await waitFor(() => expect(within(cassettesSection).queryByText('Pending Edits!')).not.toBeInTheDocument());
	});
});
