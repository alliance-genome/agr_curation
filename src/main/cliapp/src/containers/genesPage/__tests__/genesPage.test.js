import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { GenesTable } from "../GenesTable";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData.js";

describe("<GenesTable />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesTable />);
		});
		
		await waitFor(() => {
			expect(result);
		});

	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesTable />);
		});
		const tableTitle = await result.findByText(/Genes Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("Contains Correct Table Data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GenesTable />);
		});
		const curie = await result.findByText(/Foxa2/i);
		expect(curie).toBeInTheDocument();
	});
});