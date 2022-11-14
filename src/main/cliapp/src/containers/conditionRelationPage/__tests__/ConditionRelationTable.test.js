import React from "react";
import {render, screen, waitFor} from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils'
import { ConditionRelationPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler } from "../../../tools/jest/commonMswhandlers";
import { data, mockSettingsData } from "../mockData/mockData";


describe("<ConditionRelationPage />", () => {
	beforeEach(() => {
		setLocalStorage('ExperimentsTableSettings', mockSettingsData);
		setupFindHandler();
		setupSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		const result = renderWithClient(<ConditionRelationPage />);
		expect(result);
	});

	it("Contains Correct Table Name", async () => {
		const result = renderWithClient(<ConditionRelationPage />);
		const tableTitle = await result.getByText(/Experiments Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains data", async () => {
		const result = renderWithClient(<ConditionRelationPage />);
		const handleTd = await result.findByText("Standard");
		const referenceTd = await result.findByText(/PMID:28806732/i);
		const relationTd = await result.findByText(/has_condition/i);
		const exConTd = await result.findByText(/standard conditions/i);

		await waitFor(() => {
			expect(handleTd).toBeInTheDocument()
			expect(referenceTd).toBeInTheDocument()
			expect(relationTd).toBeInTheDocument()
			expect(exConTd).toBeInTheDocument()
		});
	});
});
