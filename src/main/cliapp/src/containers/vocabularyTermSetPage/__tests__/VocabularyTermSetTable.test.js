import React from "react";
import { act } from "react-dom/test-utils";
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
		let result;
		act(() => {
			result = renderWithClient(<VocabularyTermSetPage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	//skipping for now
	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<VocabularyTermSetPage />);
		});

		const tableTitle = await result.findByText(/Vocabulary Term Sets Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	//skipping 
	it.skip("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<VocabularyTermSetPage />);
		});
		
		const nameTd = await result.findByText(/AGM disease relations/i);
		await waitFor(() => {
			expect(nameTd).toBeInTheDocument();

		});
	});
});
