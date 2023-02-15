import React from "react";
import { within, screen, waitFor } from "@testing-library/react";
import { act } from "react-dom/test-utils";
import userEvent from "@testing-library/user-event";
import { renderWithClient } from '../../../tools/jest/utils';
import { DashboardPage } from "../index";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler, setupSiteSummaryHandler } from "../../../tools/jest/commonMswhandlers";
import { data, mockSettingsData } from "../mockData/mockData";


describe("<DashboardPage />", () => {
	beforeEach(() => {
		setLocalStorage('DashboardTableSettings', mockSettingsData);
        setupFindHandler();
        setupSearchHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSiteSummaryHandler(data);
	});

	it("Renders without crashing", async () => {
		const result = renderWithClient(<DashboardPage />);
		expect(result);
	});

});
