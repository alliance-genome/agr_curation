import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithClient } from '../../../tools/jest/utils';
import { SequenceTargetingReagentsTable } from '../SequenceTargetingReagentsTable';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';

describe('<SequenceTargetingReagentsTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Contains Correct Table Name', async () => {
		const result = await renderWithClient(
			<BrowserRouter>
				<SequenceTargetingReagentsTable />
			</BrowserRouter>
		);
		expect(await result.findByText(/Sequence Targeting Reagents Table/i)).toBeInTheDocument();
	});
});
