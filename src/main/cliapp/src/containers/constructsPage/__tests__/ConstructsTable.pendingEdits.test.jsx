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

vi.mock('../../../service/ConstructService', () => ({
	ConstructService: class {
		saveConstruct = vi.fn();
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

const { ConstructsTable } = await import('../ConstructsTable');

const renderTable = () =>
	renderWithClient(
		<BrowserRouter>
			<ConstructsTable />
		</BrowserRouter>
	);

// Puts the first construct's row in edit mode, as the pencil in its row does
const editRow = async (user) => {
	await screen.findAllByText('KP273', {}, { timeout: 5000 });
	await user.click(document.querySelector('.p-row-editor-init'));
	return (await screen.findByRole('button', { name: 'Synonyms(1)' })).closest('tr');
};

const keepEditsIn = async (user, heading) => {
	const dialog = (await screen.findByRole('heading', { name: heading })).closest('.p-dialog');
	// The pop-up copies the row's values in onShow, which runs as the open transition ends.
	await waitFor(() => expect(dialog).toHaveClass('p-dialog-enter-done'));
	await user.click(within(dialog).getByRole('button', { name: 'Keep Edits' }));
	await waitFor(() => expect(dialog).not.toBeInTheDocument());
};

const cellWith = (row, buttonName) => within(row).getByRole('button', { name: buttonName }).closest('td');

describe('<ConstructsTable /> pending edits', () => {
	beforeEach(() => {
		window.localStorage.clear();
	});

	it("keeps each pop-up's Pending Edits on the row after another pop-up's Keep Edits", async () => {
		const user = userEvent.setup();
		renderTable();
		const row = await editRow(user);

		await user.click(within(row).getByRole('button', { name: 'Add Note' }));
		await keepEditsIn(user, 'Related Notes');
		await waitFor(() => expect(cellWith(row, 'Add Note').textContent).toContain('Pending Edits!'));

		await user.click(within(row).getByRole('button', { name: 'Synonyms(1)' }));
		await keepEditsIn(user, 'Synonyms');

		await waitFor(() => expect(cellWith(row, 'Synonyms(1)').textContent).toContain('Pending Edits!'));
		expect(cellWith(row, 'Add Note').textContent).toContain('Pending Edits!');
	});
});
