import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { VocabularyTermSetPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<VocabularyTermSetPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<VocabularyTermSetPage />);
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<VocabularyTermSetPage />);

		const tableTitle = await result.findByText(/Vocabulary Term Sets Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<VocabularyTermSetPage />);
		
		const nameTd = await result.findByText(/Symbol name types/i);
		const vocabularyTd = await result.findByText("Name type");
		const memberTermsTd = await result.findByText(/nomenclature_symbol/i);
		const descriptionTd = await result.findByText(/Name types that are valid for symbols/i);

		await waitFor(() => {
			expect(nameTd).toBeInTheDocument();
			expect(vocabularyTd).toBeInTheDocument();
			expect(memberTermsTd).toBeInTheDocument();
			expect(descriptionTd).toBeInTheDocument();
		});
	});
});
