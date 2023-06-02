import React from "react";
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
		let result = await renderWithClient(<ExperimentalConditionsPage />);
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<ExperimentalConditionsPage />);

		const tableTitle = await result.findByText(/Experimental Conditions Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<ExperimentalConditionsPage />);

		const uniqueIdTd = await result.findByText("ZECO:0000105|ZECO:0000230|WBbt:0004809|CHEBI:27214|GO:0004705|NCBITaxon:1781");
		const conditionSummaryTd = await result.findByText(/biological treatment:bacterial treatment/i);
		const conditionClassTd = await result.findByText("biological treatment (ZECO:0000105)");
		const conditionTermTd = await result.findByText("bacterial treatment by injection (ZECO:0000230)");
		const conditionGeneOntologyTd = await result.findByText("JUN kinase activity (GO:0004705)");
		const conditionChemicalTd = await result.findByText("uranium atom (CHEBI:27214)");
		const conditionAnatomyTd = await result.findByText("F.lvv (WBbt:0004809)");
		const conditionTaxonTd = await result.findByText("Mycobacterium marinum (NCBITaxon:1781)");
		const conditionQuantityTd = await result.findByText("1");
		const conditionFreeTextTd = await result.findByText("test free text");
		const internalTd = await result.findByText("false");

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
			expect(conditionSummaryTd).toBeInTheDocument();
			expect(conditionClassTd).toBeInTheDocument();
			expect(conditionTermTd).toBeInTheDocument();
			expect(conditionGeneOntologyTd).toBeInTheDocument();
			expect(conditionChemicalTd).toBeInTheDocument();
			expect(conditionAnatomyTd).toBeInTheDocument();
			expect(conditionTaxonTd).toBeInTheDocument();
			expect(conditionQuantityTd).toBeInTheDocument();
			expect(conditionFreeTextTd).toBeInTheDocument();
			expect(internalTd).toBeInTheDocument();
		});
	});
});
