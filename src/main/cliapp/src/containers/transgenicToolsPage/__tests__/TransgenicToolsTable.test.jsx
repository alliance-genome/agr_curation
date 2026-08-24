import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { waitFor } from '@testing-library/react';
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

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<TransgenicToolsTable />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<TransgenicToolsTable />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/TransgenicTools Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it.skip('Contains Correct Table Data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<TransgenicToolsTable />
			</BrowserRouter>
		);

		const primaryExternalIdTd = await result.findByText(/WB:TransgenicToolTest0001/i);
		const modInternalIdTd = await result.findByText(/WBTransgenicTool00000001/i);
		const referencesTd = await result.findByText(/PMID:17486083/i);
		const updatedByCreatedByArray = await result.findAllByText('WB:curator');
		const dateCreatedTd = await result.findByText(/2010-01-02T00:00:00Z/i);
		const dateUpdatedTd = await result.findByText(/2012-08-03T01:00:00\+01:00/i);
		const symbolTd = await result.findByText(/Tg\(unc-119p::mCherry\)/);
		const nameTd = await result.findByText(/unc-119 promoter driving mCherry/);
		const synonymTd = await result.findByText(/uncMCherry/);
		const crossReferenceTd = await result.findByText(/WB:TransgenicToolTest0001/i);
		const transgenicToolUseTd = await result.findByText(/neuronal marker/i);
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
			expect(crossReferenceTd).toBeInTheDocument();
			expect(transgenicToolUseTd).toBeInTheDocument();
			expect(relatedNotesTd).toBeInTheDocument();
			expect(dataProviderTd).toBeInTheDocument();
		});
	});
});
