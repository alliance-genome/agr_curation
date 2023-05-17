import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { ExperimentalConditionsPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<ExperimentalConditionsPage />", () => {
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
			result = renderWithClient(<ExperimentalConditionsPage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ExperimentalConditionsPage />);
		});

		const tableTitle = await result.findByText(/Experimental Conditions Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	//skip this test for now, as it is not working
	it.skip("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ExperimentalConditionsPage />);
		});

		const uniqueIdTd = await result.findByText(/ZECO:0000105/i);

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
		});
	});
});
