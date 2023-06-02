import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { AllelesTable } from "../AllelesTable";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data, termData } from "../mockData/mockData.js";
import 'core-js/features/structured-clone';

//skipping for now
describe.skip("<AllelesTable />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		//controlled vocab service
		setupSearchHandler(termData);
		//table data search
		setupSearchHandler(data);
		//evidence component autocomplete
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AllelesTable />);
		});
		
		await waitFor(() => {
			expect(result);
		});

	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AllelesTable />);
		});
		const tableTitle = await result.findByText(/Alleles Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("Contains Correct Table Data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AllelesTable />);
		});

		const curieTd = await result.findByText(/FB:FBal0196303/i);
		const nameTd = await result.findByText(/Saccharomyces cerevisiae UAS construct a of Stefancsik/i);
		const symbolTd = await result.findByText(/Med<sup>UAS.cSa<\/sup>/i);
		const secondaryIdsTd = await result.findByText(/FB:FBal0123136/i);
		const taxonTd = await result.findByText(/Drosophila melanogaster/i);
		const referencesTd = await result.findByText(/AGRKB:101000000033001/i);
		const updatedByTd = await result.findByText(/FB:FB_curator/i);
		const dateUpdatedTd = await result.findByText(/2023-04-05T02:49:58.012787Z/i);
		const createdByTd = await result.findByText(/FB:FB_curator/i);
		const dateCreatedTd = await result.findByText(/2007-06-18T17:00:58.685302Z/i);
		const internalTd = await result.findByText(/false/i);
		const obsoleteTd = await result.findByText(/false/i);

		result.debug(undefined, Infinity)
		await waitFor(() => {
			expect(curieTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(symbolTd).toBeInTheDocument();
			expect(secondaryIdsTd).toBeInTheDocument();
			expect(taxonTd).toBeInTheDocument();
			expect(referencesTd).toBeInTheDocument();
			expect(updatedByTd).toBeInTheDocument();
			expect(dateUpdatedTd).toBeInTheDocument();
			expect(createdByTd).toBeInTheDocument();
			expect(dateCreatedTd).toBeInTheDocument();
			expect(internalTd).toBeInTheDocument();
			expect(obsoleteTd).toBeInTheDocument();
		});
	});
});