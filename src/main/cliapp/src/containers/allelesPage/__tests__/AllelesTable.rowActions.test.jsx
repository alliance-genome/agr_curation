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

const tableProps = vi.fn();
const deleteEntity = vi.fn();

vi.mock('../../../service/DeletionService', () => ({
	DeletionService: class {
		delete = deleteEntity;
	},
}));

// The table's rows never load in this environment, so the props it is given stand in for its row actions.
vi.mock('../../../components/GenericDataTable/GenericDataTable', () => ({
	GenericDataTable: (props) => {
		tableProps(props);
		return null;
	},
}));

const { AllelesTable } = await import('../AllelesTable');

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<AllelesTable />
		</BrowserRouter>
	);

describe('<AllelesTable /> row actions', () => {
	beforeEach(() => {
		tableProps.mockReset();
		deleteEntity.mockReset();
		deleteEntity.mockResolvedValue({ isSuccess: true, isError: false });
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

	it('Opens the create page for a copy of the row, by curie', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		latestProps().handleDuplication({ curie: 'AGRKB:101000000000001', primaryExternalId: 'WB:WBVar1' });

		expect(open).toHaveBeenCalledWith('/allele/create?from=AGRKB%3A101000000000001', '_blank');
		open.mockRestore();
	});

	it('Falls back to a MOD identifier for a row without a curie', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		latestProps().handleDuplication({ primaryExternalId: 'FB:FBal0196303' });

		expect(open).toHaveBeenCalledWith('/allele/create?from=FB%3AFBal0196303', '_blank');
		open.mockRestore();
	});

	it('Offers deletion alongside deprecation', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		expect(latestProps().deletionEnabled).toBe(true);
		expect(latestProps().deprecateOption).toBe(true);
	});

	it('Deletes a row by its curie', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		// called detached from the props, as the table calls it
		const { deletionMethod } = latestProps();
		await deletionMethod({ id: 1, curie: 'AGRKB:101000000000001', primaryExternalId: 'WB:WBVar1' });

		expect(deleteEntity).toHaveBeenCalledWith('allele', 'AGRKB:101000000000001');
	});

	it('Deletes a row without a curie by its MOD identifier', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		const { deletionMethod } = latestProps();
		await deletionMethod({ id: 1, primaryExternalId: 'WB:WBVar1' });

		expect(deleteEntity).toHaveBeenCalledWith('allele', 'WB:WBVar1');
	});

	it('Hands back the reason a deletion was refused', async () => {
		deleteEntity.mockResolvedValue({ isSuccess: false, isError: true, message: 'Allele WB:WBVar1 is in use' });
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		const { deletionMethod } = latestProps();
		const result = await deletionMethod({ id: 1, curie: 'AGRKB:101000000000001' });

		expect(result).toEqual({ isSuccess: false, isError: true, message: 'Allele WB:WBVar1 is in use' });
	});
});
