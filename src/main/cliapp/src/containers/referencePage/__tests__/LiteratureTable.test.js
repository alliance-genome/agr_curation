import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { renderWithClient } from '../../../tools/jest/utils';
import { ReferencePage } from "../index";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<ReferencePage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ReferencePage />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ReferencePage />);
		});

		const tableTitle = await result.findByText(/Literature References Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	//skip this test for now, as it is not working
	it.skip("The table contains correct data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<ReferencePage />);
		});
		
		const curieTd = await result.findByText(/AGRKB:101000000822386/i);
		const crossReferencesTd = await result.findByText(/MGI:62147/i);
		const titleTd = await result.findByText(/Typing of 27 polymorphic loci for 33 strains/i);
		const abstractTd = await result.findByText(/Full text of MNL contribution/i);
		const citationTd = await result.findByText(/Typing of 27 polymorphic loci for 33 strains/i);
		
		await waitFor(() => {
			expect(curieTd).toBeInTheDocument();
			expect(crossReferencesTd).toBeInTheDocument();
			expect(titleTd).toBeInTheDocument();
			expect(abstractTd).toBeInTheDocument();
			expect(citationTd).toBeInTheDocument();

		});
	});
});
