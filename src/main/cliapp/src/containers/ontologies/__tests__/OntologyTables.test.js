import React from "react";
import { act } from "react-dom/test-utils";
import { waitFor } from "@testing-library/react";
import { renderWithClient } from '../../../tools/jest/utils';
import { GeneralOntologyComponent } from "../GeneralOntologyComponent";
import { setLocalStorage } from "../../../tools/jest/setupTests";
import { setupSettingsHandler, setupFindHandler, setupSearchHandler, setupSaveSettingsHandler } from "../../../tools/jest/commonMswhandlers";
import 'core-js/features/structured-clone';

describe("ChEBI", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />);
		});

		const tableTitle = await result.findByText(/ChEBI Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("Diseases", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="Diseases" endpoint="doterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="Diseases" endpoint="doterm" />);
		});

		const tableTitle = await result.findByText(/Diseases Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("MA", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MA" endpoint="materm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MA" endpoint="materm" />);
		});

		const tableTitle = await result.findByText(/MA Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("ZFA", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />);
		});

		const tableTitle = await result.findByText(/ZFA Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("MP", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MP" endpoint="mpterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MP" endpoint="mpterm" />);
		});

		const tableTitle = await result.findByText(/MP Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("DAO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="DAO" endpoint="daoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="DAO" endpoint="daoterm" />);
		});

		const tableTitle = await result.findByText(/DAO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("EMAPA", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />);
		});

		const tableTitle = await result.findByText(/EMAPA Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("WBbt", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />);
		});

		const tableTitle = await result.findByText(/WBbt Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XCO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XCO" endpoint="xcoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XCO" endpoint="xcoterm" />);
		});

		const tableTitle = await result.findByText(/XCO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("RO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="RO" endpoint="roterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="RO" endpoint="roterm" />);
		});

		const tableTitle = await result.findByText(/RO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("ZECO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZECO" endpoint="zecoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZECO" endpoint="zecoterm" />);
		});

		const tableTitle = await result.findByText(/ZECO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("WBls", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBls" endpoint="wblsterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBls" endpoint="wblsterm" />);
		});

		const tableTitle = await result.findByText(/WBls Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("MmusDv", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" />);
		});

		const tableTitle = await result.findByText(/MmusDv Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("ZFS", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZFS" endpoint="zfsterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ZFS" endpoint="zfsterm" />);
		});

		const tableTitle = await result.findByText(/ZFS Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XBA", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBA" endpoint="xbaterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBA" endpoint="xbaterm" />);
		});

		const tableTitle = await result.findByText(/XBA Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XBS", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBS" endpoint="xbsterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBS" endpoint="xbsterm" />);
		});

		const tableTitle = await result.findByText(/XBS Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XPO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XPO" endpoint="xpoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XPO" endpoint="xpoterm" />);
		});

		const tableTitle = await result.findByText(/XPO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("ATP", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ATP" endpoint="atpterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="ATP" endpoint="atpterm" />);
		});

		const tableTitle = await result.findByText(/ATP Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XBED", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBED" endpoint="xbedterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XBED" endpoint="xbedterm" />);
		});

		const tableTitle = await result.findByText(/XBED Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("XSMO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" />);
		});

		const tableTitle = await result.findByText(/XSMO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("OBI", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="OBI" endpoint="obiterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="OBI" endpoint="obiterm" />);
		});

		const tableTitle = await result.findByText(/OBI Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("WBPhenotype", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBPhenotype" endpoint="wbphenotypeterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="WBPhenotype" endpoint="wbphenotypeterm" />);
		});

		const tableTitle = await result.findByText(/WBPhenotype Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("PATO", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="PATO" endpoint="patoterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="PATO" endpoint="patoterm" />);
		});

		const tableTitle = await result.findByText(/PATO Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});

describe("HP", () => {
	beforeEach(() => {
		setupFindHandler();
		setupSettingsHandler();
		setupSaveSettingsHandler();
		setupSearchHandler();
	});

	it("Renders without crashing", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="HP" endpoint="hpterm" />);
		});
		
		await waitFor(() => {
			expect(result);
		});
	});

	it("Contains Correct Table Name", async () => {
		let result;
		act(() => {
			result = renderWithClient(<GeneralOntologyComponent name="HP" endpoint="hpterm" />);
		});

		const tableTitle = await result.findByText(/HP Table/i);
		expect(tableTitle).toBeInTheDocument();
	});
});
