import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { CassettesTable } from '../CassettesTable';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';

describe('<CassettesTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<CassettesTable />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<CassettesTable />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/Cassettes Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('Has a Curie column', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<CassettesTable />
			</BrowserRouter>
		);

		expect(await result.findByRole('columnheader', { name: /^Curie/ })).toBeInTheDocument();
	});

	it('opens the create page in a new tab from New Cassette', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		const result = await renderWithClient(
			<BrowserRouter>
				<CassettesTable />
			</BrowserRouter>
		);

		await user.click(await result.findByRole('button', { name: 'New Cassette' }));

		expect(open).toHaveBeenCalledWith('/cassette/create', '_blank');
		open.mockRestore();
	});
});
