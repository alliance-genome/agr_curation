import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { VariantsTable } from '../VariantsTable';
import { setLocalStorage } from '../../../tools/jest/setupTests';
import {
	setupSettingsHandler,
	setupFindHandler,
	setupSearchHandler,
	setupSaveSettingsHandler,
} from '../../../tools/jest/commonMswhandlers';
import { data } from '../mockData/mockData.js';

describe('<VariantsTable />', () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it('Renders without crashing', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<VariantsTable />
			</BrowserRouter>
		);

		await waitFor(() => {
			expect(result);
		});
	});

	it('Contains Correct Table Name', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<VariantsTable />
			</BrowserRouter>
		);

		const tableTitle = await result.findByText(/Variants Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it('Labels the uniqueId column Concatenated ID', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<VariantsTable />
			</BrowserRouter>
		);

		const header = await result.findByText('Concatenated ID');
		expect(header).toBeInTheDocument();
		expect(result.queryByText('Unique ID')).not.toBeInTheDocument();
	});

	it.skip('Contains Correct Table Data', async () => {
		let result = await renderWithClient(
			<BrowserRouter>
				<VariantsTable />
			</BrowserRouter>
		);

		const curieTd = await result.findByText(/WB:WBVarTest0002/i);
		const uniqueIdTd = await result.findByText('NCBITaxon:6239|SO:0000694|WB');
		const taxonTd = await result.findByText(/Caenorhabditis elegans/i);
		const updatedByTd = await result.findByText(/VARIANTTEST:Person0002/i);
		const createdByTd = await result.findByText(/VARIANTTEST:Person0001/i);
		const dateUpdatedTd = await result.findByText(/2022-03-10T22:10:12Z/i);
		const dateCreatedTd = await result.findByText(/2022-03-09T22:10:12Z/i);
		const variantTypeTd = await result.findByText(/SNP/i);
		const variantStatusTd = await result.findByText(/dead/i);
		const sourceGeneralConsequenceTd = await result.findByText(/point_mutation/i);
		const referenceTd = await result.findByText(/AGRKB:000000002/i);

		await waitFor(() => {
			expect(curieTd).toBeInTheDocument();
			expect(uniqueIdTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
			expect(dateUpdatedTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
			expect(updatedByTd).toBeInTheDocument();
			expect(createdByTd).toBeInTheDocument();
			expect(variantTypeTd).toBeInTheDocument();
			expect(variantStatusTd).toBeInTheDocument();
			expect(sourceGeneralConsequenceTd).toBeInTheDocument();
			expect(referenceTd).toBeInTheDocument();
		});
	});
});
