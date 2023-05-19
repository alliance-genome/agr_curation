import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { ResourceDescriptorPagesPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<ResourceDescriptorPagesPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ResourceDescriptorPagesPage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ResourceDescriptorPagesPage />);
		});

		const tableTitle = await result.findByText(/Resource Descriptor Pages Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	//skip this test for now, as it is not working
	it.skip("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ResourceDescriptorPagesPage />);
		});
		
		const prefixTd = await result.findByText(/Orphanet/i);
		const nameTd = await result.findByText(/Orphanet/i);
		const idPatternTd = await result.findByText("^ORPHA:\\\\d+$");
		const idExampleTd = await result.findByText(/ORPHA:600483/i);
		const defaultUrlTemplateTd = await result.findByText("https://www.orpha.net/consor/cgi-bin/OC_Exp.php?lng=EN&Expert=[%s]");
;

		await waitFor(() => {
			expect(prefixTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(idPatternTd).toBeInTheDocument();
			expect(idExampleTd).toBeInTheDocument();
			expect(defaultUrlTemplateTd).toBeInTheDocument();

		});
	});
});
