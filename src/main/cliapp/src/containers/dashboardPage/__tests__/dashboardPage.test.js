import React from "react";
import { renderWithClient } from '../../../tools/jest/utils';
import { DashboardPage } from "../index";
import "../../../tools/jest/setupTests";
import { setupSiteSummaryHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import { act } from "react-dom/test-utils";


describe("<DashboardPage />", () => {
	beforeEach(() => {
		setupSiteSummaryHandler(data);
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<DashboardPage />);
		});
		await expect(result);
	});

	it("Contains the Table Headers", async () => {
		let result;
		act(() => {
			result = renderWithClient(<DashboardPage />);
		});	
		const entities = await result.getByText(/Entities/i);
		const ontologies = await result.getByText(/Ontologies/i);
		const system = await result.getByText(/System Name/i);
		expect(entities).toBeInTheDocument();
		expect(ontologies).toBeInTheDocument();
		expect(system).toBeInTheDocument();
	});
});
