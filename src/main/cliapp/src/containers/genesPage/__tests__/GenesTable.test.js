import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { GenesPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

//is having the same issue as alleles table now
describe.skip("<GenesPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesPage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesPage />);
		});

		const tableTitle = await result.findByText(/Genes Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesPage />);
		});

		const curieTd = await result.findByText(/WB:WBGene00002975/i);
		const nameTd = await result.findByText(/LEVamisole resistant 8/i);
		const symbolTd = await result.findByText(/lev-8/i); 
		const synonymsTd = await result.findByText(/acr-13/i); 
		const secondaryIdsTd = await result.findByText(/WB:WBGene00000052/i); 
		const systematicNameTd = await result.findByText(/C35C5.5/i); 
		const taxonTd = await result.findByText(/Caenorhabditis elegans/i);

		await waitFor(() => {
			expect(curieTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(synonymsTd).toBeInTheDocument();
			expect(secondaryIdsTd).toBeInTheDocument();
			expect(systematicNameTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
		});
	});

});
