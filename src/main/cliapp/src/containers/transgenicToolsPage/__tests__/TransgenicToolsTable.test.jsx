import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithClient } from '../../../tools/jest/utils';
import { TransgenicToolsTable } from '../TransgenicToolsTable';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';

describe('<TransgenicToolsTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Contains Correct Table Name', async () => {
		const result = await renderWithClient(
			<BrowserRouter>
				<TransgenicToolsTable />
			</BrowserRouter>
		);
		expect(await result.findByText(/Transgenic Tools Table/i)).toBeInTheDocument();
	});
});
