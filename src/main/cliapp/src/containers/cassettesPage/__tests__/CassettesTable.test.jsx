import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { waitFor } from '@testing-library/react';
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

	it.skip('Contains Correct Table Data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<CassettesTable />
			</BrowserRouter>
		);

		const primaryExternalIdTd = await result.findByText(/WB:CassetteTest0001/i);
		const modInternalIdTd = await result.findByText(/WBCassette00000001/i);
		const referencesTd = await result.findByText(/PMID:17486083/i);
		const updatedByCreatedByArray = await result.findAllByText('WB:curator');
		const dateCreatedTd = await result.findByText(/2010-01-02T00:00:00Z/i);
		const dateUpdatedTd = await result.findByText(/2012-08-03T01:00:00\+01:00/i);
		const symbolTd = await result.findByText(/myo-2p::GFP/);
		const nameTd = await result.findByText(/myo-2 promoter driving GFP/);
		const synonymTd = await result.findByText(/myoGFP/);
		const freeTextComponentTd = await result.findByText(/gfp/i);
		const genomicEntityComponentTd = await result.findByText(/lin-17/i);
		const relatedNotesTd = await result.findByText(/Notes \(1\)/i);
		const dataProviderTd = await result.findByText(/WB/);

		await waitFor(() => {
			expect(primaryExternalIdTd).toBeInTheDocument();
			expect(modInternalIdTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(synonymTd).toBeInTheDocument();
			expect(referencesTd).toBeInTheDocument();
			expect(updatedByCreatedByArray.length).toEqual(2);
			expect(dateUpdatedTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
			expect(freeTextComponentTd).toBeInTheDocument();
			expect(genomicEntityComponentTd).toBeInTheDocument();
			expect(relatedNotesTd).toBeInTheDocument();
			expect(dataProviderTd).toBeInTheDocument();
		});
	});
});
