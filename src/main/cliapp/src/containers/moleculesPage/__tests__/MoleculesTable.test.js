import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { MoleculesPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<MoleculesPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<MoleculesPage />);
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<MoleculesPage />);

		const tableTitle = await result.findByText(/Molecule Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<MoleculesPage />);

		const curieTd = await result.findByText(/WB:WBMol:00007937/i);
		const nameTd = await result.findByText(/frondoside A/i);
		const inchiTd = await result.findByText(/C60H96O29S.Na/i);
		const inchiKeyTd = await result.findByText(/JVAXTYZSDXFKMH-BAYUWVQRSA-M/i);
		const iupacTd = await result.findByText(/C60H95NaO29S/i)
		const smilesTd = await result.findByText(/C5CCC67C/i);

		await waitFor(() => {
			expect(curieTd).toBeInTheDocument();
			expect(nameTd).toBeInTheDocument();
			expect(inchiTd).toBeInTheDocument();
			expect(inchiKeyTd).toBeInTheDocument();
			expect(iupacTd).toBeInTheDocument();
			expect(smilesTd).toBeInTheDocument();

		});
	});

});
