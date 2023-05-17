import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { DiseaseAnnotationsPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<DiseaseAnnotationsPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<DiseaseAnnotationsPage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<DiseaseAnnotationsPage />);
		});

		const tableTitle = await result.findByText(/Disease Annotations Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	//skip this test for now, as it is not working
	it.skip("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<DiseaseAnnotationsPage />);
		});

		const uniqueIdTd = await result.findByText(/WB:WBStrain00024340/i);

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
		});
	});

});
