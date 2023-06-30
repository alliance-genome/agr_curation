import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { OrthologyPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<OrthologyPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<OrthologyPage />);
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<OrthologyPage />);

		const tableTitle = await result.findByText(/Orthology Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<OrthologyPage />);

		const subjectGeneCurieTd = await result.findByText(/SGD:S000000662/i);
		const subjectGeneSymbolTd = await result.findByText(/RAD18/);
		const subjectGeneTaxonName = await result.findByText(/Saccharomyces cerevisiae S288C/i);
		const subjectGeneTaxonCurie = await result.findByText(/NCBITaxon:559292/i);
		const objectGeneCurieTd = await result.findByText(/ZFIN:ZDB-GENE-040426-745/i);
		const objectGeneSymbolTd = await result.findByText(/rad18/);
		const objectGeneTaxonNameTd = await result.findByText(/Danio rerio/i);
		const objectGeneTaxonCurieTd = await result.findByText(/NCBITaxon:7955/i);
		const predictionMethodMatchedTd = await result.findByText(/Ensembl Compara/i);
		const predictionMethodNotMatchedTd = await result.findByText(/OMA/i);
		const predictionMethodNotCalledTd = await result.findByText(/Xenbase/i);
		

		await waitFor(() => {
			expect(subjectGeneCurieTd).toBeInTheDocument();
			expect(subjectGeneSymbolTd).toBeInTheDocument();
			expect(subjectGeneTaxonName).toBeInTheDocument();
			expect(subjectGeneTaxonCurie).toBeInTheDocument();
			expect(objectGeneCurieTd).toBeInTheDocument();
			expect(objectGeneSymbolTd).toBeInTheDocument();
			expect(objectGeneTaxonNameTd).toBeInTheDocument();
			expect(objectGeneTaxonCurieTd).toBeInTheDocument();
			expect(predictionMethodMatchedTd).toBeInTheDocument();
			expect(predictionMethodNotMatchedTd).toBeInTheDocument();
			expect(predictionMethodNotCalledTd).toBeInTheDocument();
		});
	});

});
