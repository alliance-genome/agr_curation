import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import '../../../tools/jest/setupTests';
import {
	setupFindHandler,
	setupSaveSettingsHandler,
	setupSearchHandler,
	setupSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';
import { DeletionService } from '../../../service/DeletionService';

const tableProps = vi.fn();

// The table's rows never load in this environment, so the props it is given stand in for its row actions.
vi.mock('../../../components/GenericDataTable/GenericDataTable', () => ({
	GenericDataTable: (props) => {
		tableProps(props);
		return null;
	},
}));

const { CassettesTable } = await import('../CassettesTable');

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<CassettesTable />
		</BrowserRouter>
	);

describe('<CassettesTable /> row actions', () => {
	beforeEach(() => {
		tableProps.mockReset();
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	const latestProps = () => tableProps.mock.calls.at(-1)[0];

	it('Turns on the duplicate action', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		expect(latestProps().duplicationEnabled).toBe(true);
	});

	it('Turns on deletion, offering deprecation first', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		expect(latestProps().deletionEnabled).toBe(true);
		expect(latestProps().deprecateOption).toBe(true);
	});

	it('Deletes a row through the cassette delete endpoint, by curie', async () => {
		const remove = vi.spyOn(DeletionService.prototype, 'delete').mockResolvedValue({ isSuccess: true, isError: false });
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		await latestProps().deletionMethod({ curie: 'AGRKB:121000000000001', primaryExternalId: 'FB:FBcs0000001' });

		expect(remove).toHaveBeenCalledWith('cassette', 'AGRKB:121000000000001');
		remove.mockRestore();
	});

	it('Opens the create page for a copy of the row, by curie', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		latestProps().handleDuplication({ curie: 'AGRKB:121000000000001', primaryExternalId: 'FB:FBcs0000001' });

		expect(open).toHaveBeenCalledWith('/cassette/create?from=AGRKB%3A121000000000001', '_blank');
		open.mockRestore();
	});

	it('Falls back to a MOD identifier for a row without a curie', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		latestProps().handleDuplication({ primaryExternalId: 'FB:FBcs0000001' });

		expect(open).toHaveBeenCalledWith('/cassette/create?from=FB%3AFBcs0000001', '_blank');
		open.mockRestore();
	});
});
