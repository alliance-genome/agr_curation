import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

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

vi.mock('../../../service/AlleleService', () => ({
	AlleleService: class {
		saveAllele = vi.fn();
	},
}));

// The pop-ups validate what they hand back on Keep Edits.
vi.mock('../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = vi.fn(() => Promise.resolve({ isSuccess: true, isError: false, data: {} }));
	},
}));

vi.mock('../../../service/useVocabularyTermSetService');
vi.mock('../../../service/useControlledVocabularyService');

const { AllelesTable } = await import('../AllelesTable');

const SECONDARY_IDS = /^Secondary IDs\(\d+\)/;
const ADD_MUTATION_TYPE = 'Add Mutation Type';

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<AllelesTable />
		</BrowserRouter>
	);

// Puts the first allele's row in edit mode, as the pencil in its row does.
const editRow = async (user) => {
	await waitFor(() => expect(document.querySelector('.p-row-editor-init')).not.toBeNull(), { timeout: 5000 });
	await user.click(document.querySelector('.p-row-editor-init'));
	return (await screen.findByRole('button', { name: SECONDARY_IDS })).closest('tr');
};

const keepEditsIn = async (user, heading) => {
	const dialog = (await screen.findByRole('heading', { name: heading })).closest('.p-dialog');
	// The pop-up copies the row's values in onShow, which runs as the open transition ends.
	await waitFor(() => expect(dialog).toHaveClass('p-dialog-enter-done'));
	await user.click(within(dialog).getByRole('button', { name: 'Keep Edits' }));
	await waitFor(() => expect(dialog).not.toBeInTheDocument());
};

const cellWith = (row, buttonName) => within(row).getByRole('button', { name: buttonName }).closest('td');

describe('<AllelesTable /> pending edits', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.clear();
	});

	it("keeps each pop-up's Pending Edits on the row after another pop-up's Keep Edits", async () => {
		const user = userEvent.setup();
		renderTable();
		const row = await editRow(user);

		await user.click(within(row).getByRole('button', { name: SECONDARY_IDS }));
		await keepEditsIn(user, 'Secondary IDs');
		await waitFor(() => expect(cellWith(row, SECONDARY_IDS).textContent).toContain('Pending Edits!'));

		await user.click(within(row).getByRole('button', { name: ADD_MUTATION_TYPE }));
		await keepEditsIn(user, 'Mutation Types');

		await waitFor(() => expect(cellWith(row, ADD_MUTATION_TYPE).textContent).toContain('Pending Edits!'));
		expect(cellWith(row, SECONDARY_IDS).textContent).toContain('Pending Edits!');
	});
});
