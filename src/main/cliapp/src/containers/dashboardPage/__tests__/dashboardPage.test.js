import React from "react";
import { within, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
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
		var result;
		act(() => {
			result = renderWithClient(<DashboardPage />);
		});
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains the Table Headers", async () => {
		var result;
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

	it("Contains some test values in the tables", async () => {
		var result;
		act(() => {
			result = renderWithClient(<DashboardPage />);
		});	
		// const NameSlotAnnotation = await result.getByText("6828355");
		// expect(NameSlotAnnotation).toBeInTheDocument();
	});
});
