import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { VocabulariesPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<VocabulariesPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<VocabulariesPage />);

		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<VocabulariesPage />);

		const tableTitle = await result.findByText(/Vocabularies Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<VocabulariesPage />);

		const nameTd = await result.findByText(/Name type/i);
		const descriptionTd = await result.findByText(/Type of name represented by a name annotation/i);
		const obsoleteTd = await result.findByText(/false/i);

		await waitFor(() => {
			expect(nameTd).toBeInTheDocument();
			expect(descriptionTd).toBeInTheDocument();
			expect(obsoleteTd).toBeInTheDocument();
		});
	});
});
