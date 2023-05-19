import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import ControlledVocabularyPage from "../ControlledVocabularyPage";
import { setupSettingsHandler, setupFindHandler, setupVocabularyHandler, setupSearchHandler } from "../../../tools/jest/commonMswhandlers";
import { data, termData } from "../mockData/mockData.js";
import 'core-js/features/structured-clone';

//skipping for now
describe.skip("<ControlledVocabularyPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupVocabularyHandler();
		setupSearchHandler(termData);
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<ControlledVocabularyPage />);
		
		await waitFor(() => {
			expect(result);
		});

	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<ControlledVocabularyPage />);

		const tableTitle = await result.findByText(/Controlled Vocabulary Terms Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("Contains Correct Table Data", async () => {
		let result = await renderWithClient(<ControlledVocabularyPage />);

		const idTd = await result.findByText(/6363427/i);
		const nameTd = await result.findByText(/ECO:0007014/i);
		const abbreviationTd = await result.findByText(/CEC/i);
		const vocabularyTd = await result.findByText(/AGR disease annotation ECO terms/i);
		const definitionTd = await result.findByText(/ECO:0007014/i);
		const obsoleteTd = await result.findByText(/false/i);

		expect(idTd).toBeInTheDocument();
		expect(nameTd).toBeInTheDocument();
		expect(abbreviationTd).toBeInTheDocument();
		expect(vocabularyTd).toBeInTheDocument();
		expect(definitionTd).toBeInTheDocument();
		expect(obsoleteTd).toBeInTheDocument();
	});
});