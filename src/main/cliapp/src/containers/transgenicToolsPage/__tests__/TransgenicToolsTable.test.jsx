import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { data } from '../mockData/mockData.js';

const { saveTransgenicTool, getTransgenicToolAssociations } = vi.hoisted(() => ({
	saveTransgenicTool: vi.fn(),
	getTransgenicToolAssociations: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/SearchService', async () => {
	const { data: searchResults } = await import('../mockData/mockData.js');
	return {
		SearchService: class {
			search = vi.fn(() => Promise.resolve(structuredClone(searchResults)));
			find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		},
	};
});

vi.mock('../../../service/TransgenicToolService', () => ({
	TransgenicToolService: class {
		saveTransgenicTool = saveTransgenicTool;
		getTransgenicToolAssociations = getTransgenicToolAssociations;
	},
}));

vi.mock('../../../service/useVocabularyTermSetService');
vi.mock('../../../service/useControlledVocabularyService');

const { TransgenicToolsTable } = await import('../TransgenicToolsTable');

const tool = data.results[0];

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<TransgenicToolsTable />
		</BrowserRouter>
	);

// Puts the tool's row in edit mode, as the pencil in its row does
const editRow = async (user) => {
	await screen.findByText('superfolder GFP', {}, { timeout: 5000 });
	await user.click(document.querySelector('.p-row-editor-init'));
	return screen.getByRole('button', { name: 'Uses(1)' }).closest('tr');
};

describe('<TransgenicToolsTable />', () => {
	beforeEach(() => {
		window.localStorage.clear();
		saveTransgenicTool.mockReset();
		getTransgenicToolAssociations.mockReset();
		saveTransgenicTool.mockImplementation((payload) => Promise.resolve({ data: { entity: payload } }));
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [] } });
	});

	it('Contains Correct Table Name', async () => {
		renderTable();
		expect(await screen.findByText(/Transgenic Tools Table/i)).toBeInTheDocument();
	});

	it("opens the ticket's columns for editing in a row", async () => {
		const user = userEvent.setup();
		renderTable();
		const row = await editRow(user);

		['Synonyms(1)', 'Uses(1)', 'Associations(1)', 'Notes(1)', 'Add Cross Reference'].forEach((label) =>
			expect(within(row).getByRole('button', { name: label })).toBeInTheDocument()
		);
		expect(within(row).getByRole('button', { name: 'sfGFP' })).toBeInTheDocument();
		expect(within(row).getByRole('button', { name: 'superfolder GFP' })).toBeInTheDocument();
	});

	it('saves the row as the detail page sends the tool, without the associations summary', async () => {
		const user = userEvent.setup();
		renderTable();
		const row = await editRow(user);

		await user.click(row.querySelector('.p-row-editor-save'));

		await waitFor(() => expect(saveTransgenicTool).toHaveBeenCalledTimes(1));
		const [payload] = saveTransgenicTool.mock.calls[0];
		expect(payload.id).toBe(tool.id);
		expect(payload).not.toHaveProperty('associatedTransgenicTools');
		expect(payload.transgenicToolUses[0].uses[0].curie).toBe('FBcv:0005012');
		expect(await screen.findByText('Row Updated')).toBeInTheDocument();
	});

	it("opens the tool's associations for editing through their own endpoint", async () => {
		const user = userEvent.setup();
		renderTable();
		const row = await editRow(user);

		await user.click(within(row).getByRole('button', { name: 'Associations(1)' }));

		await waitFor(() => expect(getTransgenicToolAssociations).toHaveBeenCalledWith(tool.id));
		expect(await screen.findByRole('button', { name: 'Add Association' })).toBeInTheDocument();
	});
});
