import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { CassettesDialog } from '../CassettesDialog';
import { data } from '../../cassettesPage/mockData/mockData.js';

describe('<CassettesDialog />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('shows the Cassettes table', async () => {
		const result = await renderWithClient(
			<BrowserRouter>
				<CassettesDialog
					originalCassettesData={{
						dialog: true,
						originalCassettes: [{ id: 1, constructCassetteAssociationObject: { id: 200096001 } }],
					}}
					setOriginalCassettesData={() => {}}
				/>
			</BrowserRouter>
		);

		expect(await result.findByText(/Construct Cassettes Table/i)).toBeInTheDocument();
	});

	it('does not offer editing or creating cassettes', async () => {
		const result = await renderWithClient(
			<BrowserRouter>
				<CassettesDialog
					originalCassettesData={{
						dialog: true,
						originalCassettes: [{ id: 1, constructCassetteAssociationObject: { id: 200096001 } }],
					}}
					setOriginalCassettesData={() => {}}
				/>
			</BrowserRouter>
		);

		await result.findByText(/Construct Cassettes Table/i);
		expect(result.queryByText(/New Cassette/i)).not.toBeInTheDocument();
		expect(document.querySelector('.action-column.visible')).toBeNull();
	});
});
