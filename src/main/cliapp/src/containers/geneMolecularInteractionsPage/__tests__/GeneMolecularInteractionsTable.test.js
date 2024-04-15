import React from "react";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { GeneMolecularInteractionsPage } from "../index";
import '../../../tools/jest/setupTests';
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data } from "../mockData/mockData";
import 'core-js/features/structured-clone';

describe("<GeneMolecularInteractionsPage />", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler(data);
	});

	it("Renders without crashing", async () => {
		let result = await renderWithClient(<GeneMolecularInteractionsPage />);
		
		await waitFor(() => {
			expect(result);
		});
	}, 10000);

	it("Contains Correct Table Name", async () => {
		let result = await renderWithClient(<GeneMolecularInteractionsPage />);

		const tableTitle = await result.findByText(/Gene Molecular Interactions Table/i);
		expect(tableTitle).toBeInTheDocument();
	}, 10000);

	it("The table contains correct data", async () => {
		let result = await renderWithClient(<GeneMolecularInteractionsPage />);

		const uniqueIdTd = await result.findByText("WB:WBGene00012970|WB:WBGene00006765|AGRKB:101000000620882|MI:0914|MI:0498|MI:0496|MI:0326|MI:0326|MI:0004");
		const interactionIdTd = await result.findByText("mockInteractionId");
		const interactorATd = await result.findByText(/Y48A6B\.9/i);
		const relationTd = await result.findByText("physically_interacts_with");
		const interactorBTd = await result.findByText(/unc\-29/i);
		const interactionSourceTd = await result.findByText("wormbase (MI:0487)");
		const interactionTypeTd = await result.findByText("association (MI:0914)");
		const interactorARoleTd = await result.findByText("prey (MI:0498)");
		const interactorBRoleTd = await result.findByText("bait (MI:0496)");
		const interactorATypeTd = await result.findByText("protein (MI:0326)");
		const interactorBTypeTd = await result.findByText("proteinTest (MI:0326a)");
		const aggregationDatabaseTypeTd = await result.findByText("wormbaseTest (MI:0487)");
		const detectionMethodTd = await result.findByText("affinity chromatography technology (MI:0004)");

		await waitFor(() => {
			expect(uniqueIdTd).toBeInTheDocument();
			expect(interactionIdTd).toBeInTheDocument();
			expect(interactorATd).toBeInTheDocument();
			expect(relationTd).toBeInTheDocument();
			expect(interactorBTd).toBeInTheDocument();
			expect(interactionSourceTd).toBeInTheDocument();
			expect(interactionTypeTd).toBeInTheDocument();
			expect(interactorARoleTd).toBeInTheDocument();
			expect(interactorBRoleTd).toBeInTheDocument();
			expect(interactorATypeTd).toBeInTheDocument();
			expect(interactorBTypeTd).toBeInTheDocument();
			expect(aggregationDatabaseTypeTd).toBeInTheDocument();
			expect(detectionMethodTd).toBeInTheDocument();
		});
	}, 10000);

});
