import React from "react";
import { within, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { renderWithClient } from '../../../tools/jest/utils';
import { ConditionRelationPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import { data, mockSettingsData } from "../mockData/mockData";


describe("<ConditionRelationPage />", () => {
	beforeEach(() => {
		setLocalStorage('ExperimentsTableSettings', mockSettingsData);
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
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
			expect(handleTd).toBeInTheDocument();
			expect(referenceTd).toBeInTheDocument();
			expect(relationTd).toBeInTheDocument();
			expect(exConTd).toBeInTheDocument();
		});
	});

	it("Has the same text in edit mode", async () => {

		//jestdom doesn't support global.structuredClone so it needs to be mocked
		global.structuredClone = jest.fn(val => {
			return JSON.parse(JSON.stringify(val));
		});

		renderWithClient(<ConditionRelationPage />);

		const user = userEvent.setup();

		let cell;
		let editButton;

		await waitFor(() => {
			cell = screen.getAllByRole('cell');
			editButton = within(cell[0]).getByRole('button');
		});

		await user.click(editButton);

		let handleInput = screen.getByRole('textbox', { name: /handle/i });
		let referenceAutocomplete = screen.getByRole('combobox', { name: /singleReference/i });
		let relationDropdown = screen.getByRole('button', { name: /has_condition/i });
		let experimentalConditionsAutocomplete = screen.getByRole('combobox', { name: /conditions/i });
		
		expect(handleInput.value).toEqual('Standard');
		expect(referenceAutocomplete.value).toEqual("PMID:28806732 (DOI:10.1371/journal.pgen.1006959|PMCID:PMC5570503|ZFIN:ZDB-PUB-170815-1|AGRKB:101000000675992)");
		expect(relationDropdown.previousSibling.firstChild.nodeValue).toEqual("has_condition");
		expect(experimentalConditionsAutocomplete.parentElement.previousSibling.firstChild.firstChild.nodeValue).toEqual("standard conditions");
	});
});
