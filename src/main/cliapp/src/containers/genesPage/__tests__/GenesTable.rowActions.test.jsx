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

const { GenesTable } = await import('../GenesTable');

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<GenesTable />
		</BrowserRouter>
	);

describe('<GenesTable /> row actions', () => {
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

	it('opens the create page for a copy of the row, by curie', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		expect(latestProps().duplicationEnabled).toBe(true);
		latestProps().handleDuplication({ id: 1, curie: 'AGRKB:105000000000001', primaryExternalId: 'WB:WBGene00000001' });

		expect(open).toHaveBeenCalledWith(`/gene/create?from=${encodeURIComponent('AGRKB:105000000000001')}`, '_blank');
		open.mockRestore();
	});

	it('copies a row without a curie by its MOD identifier', async () => {
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		latestProps().handleDuplication({ id: 1, primaryExternalId: 'WB:WBGene00000001' });

		expect(open).toHaveBeenCalledWith(`/gene/create?from=${encodeURIComponent('WB:WBGene00000001')}`, '_blank');
		open.mockRestore();
	});

	it('offers deletion alongside deprecation', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		expect(latestProps().deletionEnabled).toBe(true);
		expect(latestProps().deprecateOption).toBe(true);
	});

	it('deletes a row by its curie', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		// called detached from the props, as the table calls it
		const { deletionMethod } = latestProps();
		await deletionMethod({ id: 1, curie: 'AGRKB:105000000000001', primaryExternalId: 'WB:WBGene00000001' });

		expect(deleteEntity).toHaveBeenCalledWith('gene', 'AGRKB:105000000000001');
	});

	it('deletes a row without a curie by its MOD identifier', async () => {
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		const { deletionMethod } = latestProps();
		await deletionMethod({ id: 1, primaryExternalId: 'WB:WBGene00000001' });

		expect(deleteEntity).toHaveBeenCalledWith('gene', 'WB:WBGene00000001');
	});

	it('hands back the reason a deletion was refused', async () => {
		deleteEntity.mockResolvedValue({ isSuccess: false, isError: true, message: 'Gene WB:WBGene00000001 is in use' });
		await renderTable();

		await waitFor(() => expect(tableProps).toHaveBeenCalled());
		const { deletionMethod } = latestProps();
		const result = await deletionMethod({ id: 1, curie: 'AGRKB:105000000000001' });

		expect(result).toEqual({ isSuccess: false, isError: true, message: 'Gene WB:WBGene00000001 is in use' });
	});
});
