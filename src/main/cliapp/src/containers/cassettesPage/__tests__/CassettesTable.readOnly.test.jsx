import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';

// msw cannot intercept this app's fetch based ApiClient, so stub the search directly.
vi.mock('../../../service/SearchService', async () => {
	const { data: searchResults } = await import('../mockData/mockData.js');
	return {
		SearchService: class {
			search = vi.fn(() => Promise.resolve(structuredClone(searchResults)));
			find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		},
	};
});

vi.mock('../../../service/useVocabularyTermSetService');
vi.mock('../../../service/useControlledVocabularyService');

const { CassettesTable } = await import('../CassettesTable');

const renderTable = (props) =>
	renderWithClient(
		<BrowserRouter>
			<CassettesTable {...props} />
		</BrowserRouter>
	);

// The cells shown in the row of column titles, the row of filters and the first data row. A cell with the hidden
// class is in the DOM but not on screen, so it takes no column.
const shownCells = (selector) =>
	[...document.querySelectorAll(selector)].filter((cell) => !cell.classList.contains('hidden'));
const titleCells = () => shownCells('.p-datatable-thead > tr:nth-child(1) > th');
const filterCells = () => shownCells('.p-datatable-thead > tr:nth-child(2) > th');
const firstRowCells = () => shownCells('.p-datatable-tbody > tr:first-child > td');

const expectAlignedColumns = () => {
	expect(titleCells().length).toBeGreaterThan(0);
	expect(filterCells()).toHaveLength(titleCells().length);
	expect(firstRowCells()).toHaveLength(titleCells().length);
};

// SCRUM-6933: the Constructs table's Cassettes pop-up shows this table read-only
describe('<CassettesTable /> read-only', () => {
	beforeEach(() => window.localStorage.clear());

	it('lines the filters and every cell of a row up with their column titles', async () => {
		renderTable({ tableName: 'Construct Cassettes', isEditable: false });
		await screen.findByText('croc[JF02416]', {}, { timeout: 5000 });

		expectAlignedColumns();
	});

	it('offers no row editing, deletion or duplication', async () => {
		renderTable({ tableName: 'Construct Cassettes', isEditable: false });
		await screen.findByText('croc[JF02416]', {}, { timeout: 5000 });

		expect(document.querySelector('.p-row-editor-init')).toBeNull();
		// of the row actions only the link to the cassette's detail page is left; no hidden delete or duplicate cell
		const actionCells = document.querySelectorAll('.p-datatable-tbody > tr:first-child > td.action-column');
		expect(actionCells).toHaveLength(1);
		expect(actionCells[0].classList.contains('hidden')).toBe(false);
	});

	it('keeps the row actions of the editable table, lined up with their headers', async () => {
		renderTable();
		await screen.findByText('croc[JF02416]', {}, { timeout: 5000 });

		expect(document.querySelector('.p-row-editor-init')).not.toBeNull();
		expectAlignedColumns();
	});
});
