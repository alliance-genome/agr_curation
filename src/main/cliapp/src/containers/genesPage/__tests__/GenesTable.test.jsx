import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { renderWithClient } from '../../../tools/jest/utils';
import { GenesPage } from '../index';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData';

describe('<GenesPage />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<GenesPage />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<GenesPage />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/Genes Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('Opens the create page in a new tab from the New Gene button', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);

		await renderWithClient(
			<BrowserRouter>
				<GenesPage />
			</BrowserRouter>
		);

		await user.click(await screen.findByRole('button', { name: /New Gene/i }));

		expect(open).toHaveBeenCalledWith('/gene/create', '_blank');
		open.mockRestore();
	});

	it.skip('The table contains correct data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<GenesPage />
			</BrowserRouter>
		);

		const primaryExternalIdTd = await result.findByText(/WB:WBGene00003771/i);
		const nameTd = await result.findByText(/LEVamisole resistant 8/i);
		const symbolTd = await result.findByText(/lev-8/i);
		const synonymsTd = await result.findByText(/acr-13/i);
		const secondaryIdsTd = await result.findByText(/WB:WBGene00000052/i);
		const systematicNameTd = await result.findByText('C35C5.5');
		const taxonTd = await result.findByText(/Caenorhabditis elegans/i);
		const xrefsTd = await result.findByText(/WBGene00003771Xref/);
		const geneTypeTd = await result.findByText(/protein_coding_gene/i);
		const gcrpXrefDisplayTd = await result.findByText(/GCRPXref/i);
		const gcrpXrefCurieTd = await result.findByText(/UniProt:GCRP01/i);

		await waitFor(() => {
			expect(primaryExternalIdTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(synonymsTd).toBeInTheDocument();
			expect(secondaryIdsTd).toBeInTheDocument();
			expect(systematicNameTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
			expect(xrefsTd).toBeInTheDocument();
			expect(geneTypeTd).toBeInTheDocument();
			expect(gcrpXrefDisplayTd).toBeInTheDocument();
			expect(gcrpXrefCurieTd).toBeInTheDocument();
		});
	});
});
