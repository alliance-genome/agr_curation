import React from "react";
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
		let result = await renderWithClient(<ResourceDescriptorPagesPage />);
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<ResourceDescriptorPagesPage />);

		const tableTitle = await result.findByText(/Resource Descriptor Pages Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<ResourceDescriptorPagesPage />);
		
		const prefixTd = await result.findByText(/ZFIN_prefix/i);
		const nameTd = await result.findByText(/ZFIN_name/i);
		const urlTemplateTd = await result.findByText("https://zfin.org/[%s]/wt-expression");
		const pageDescriptionTd = await result.findByText(/Wild type expression data/i);
;

		await waitFor(() => {
			expect(prefixTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(urlTemplateTd).toBeInTheDocument();
			expect(pageDescriptionTd).toBeInTheDocument();
		});
	});
});
