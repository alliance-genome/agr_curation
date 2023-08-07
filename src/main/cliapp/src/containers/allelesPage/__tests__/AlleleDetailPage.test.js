import React from "react";
import { BrowserRouter } from "react-router-dom/cjs/react-router-dom";
import { within, screen, waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import AlleleDetailPage from '../AlleleDetailPage';
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupGetEntityHandler } from "../../../tools/jest/commonMswhandlers";
import { alleleDetailData } from "../mockData/mockData.js";
import 'core-js/features/structured-clone';

describe("<AlleleDetailPage />", () => {
	beforeEach(() => {
		setupGetEntityHandler(alleleDetailData);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<BrowserRouter><AlleleDetailPage /></BrowserRouter>);
		
		await waitFor(() => {
			expect(result);
		});

	});

	it("Contains correct data in field", async () => {
		let result = await renderWithClient(<BrowserRouter><AlleleDetailPage /></BrowserRouter>);

		let taxonInput = await result.findByRole('combobox', {name: 'taxon-input'});
		console.log(taxonInput);
		
		expect(taxonInput.value).toEqual('NCBITaxon:10090');

	});

	//expect taxon curie (or name?) to be in the input field
	//expect taxon additional field data to be formatted properly
	//expect the page to still render if there is no allele (probably should have a message)
	//expect the field to be blank if there is an allele but no taxon

});