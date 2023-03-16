import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { AffectedGenomicModelTable } from "../AffectedGenomicModelTable";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData.js";
import 'core-js/features/structured-clone';

describe("<AffectedGenomicModelTable />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AffectedGenomicModelTable />);
		});
		
		await waitFor(() => {
			expect(result);
		});

	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AffectedGenomicModelTable />);
		});
		const tableTitle = await result.findByText(/Affected Genomic Models Table/i);
		expect(tableTitle).toBeInTheDocument();
	});

	it("Contains Correct Table Data", async () => {
		let result;
		act(() => {
			result = renderWithClient(<AffectedGenomicModelTable />);
		});
		const curie = await result.findByText(/WB:WBStrain00051221/i);
		expect(curie).toBeInTheDocument();
	});
});