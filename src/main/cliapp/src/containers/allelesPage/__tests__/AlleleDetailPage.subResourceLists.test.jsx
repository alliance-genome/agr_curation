import React from 'react';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
	alleleRejection,
	FORM_LOAD_WAIT,
	markAlleleExtinct,
	renderInRouter,
	storedAllele,
	OUTSIDE_SAVE_PROGRESS,
} from './alleleTestUtils';
import { getCellByHeader } from './alleleFormTestUtils';

const { getAllele, saveAlleleDetail, getRows, replaceRows } = vi.hoisted(() => ({
	getAllele: vi.fn(),
	saveAlleleDetail: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
}));

const LISTS = [
	{
		path: 'synonyms',
		label: 'synonyms',
		title: 'Synonyms',
		errorField: 'alleleSynonyms',
		rows: [
			{ id: 31, internal: false, displayText: 'syn-1', formatText: 'syn-1', nameType: { name: 'systematic_name' } },
			{ id: 32, internal: false, displayText: 'syn-2', formatText: 'syn-2', nameType: { name: 'systematic_name' } },
		],
	},
	{
		path: 'secondaryids',
		label: 'secondary IDs',
		title: 'Secondary IDs',
		errorField: 'alleleSecondaryIds',
		rows: [
			{ id: 41, internal: false, secondaryId: 'WB:WBVar0001' },
			{ id: 42, internal: false, secondaryId: 'WB:WBVar0002' },
		],
	},
	{
		path: 'nomenclatureevents',
		label: 'nomenclature events',
		title: 'Nomenclature Events',
		errorField: 'alleleNomenclatureEvents',
		rows: [
			{ id: 51, internal: false, obsolete: false, nomenclatureEvent: { name: 'data_merged' } },
			{ id: 52, internal: false, obsolete: false, nomenclatureEvent: { name: 'symbol_updated' } },
		],
	},
	{
		path: 'mutationtypes',
		label: 'mutation types',
		title: 'Mutation Types',
		errorField: 'alleleMutationTypes',
		rows: [
			{ id: 61, internal: false, mutationTypes: [{ curie: 'SO:0001', name: 'point_mutation' }] },
			{ id: 62, internal: false, mutationTypes: [{ curie: 'SO:0002', name: 'deletion' }] },
		],
	},
	{
		path: 'functionalimpacts',
		label: 'functional impacts',
		title: 'Functional Impacts',
		errorField: 'alleleFunctionalImpacts',
		rows: [
			{ id: 71, internal: false, functionalImpacts: [{ name: 'amorph' }], phenotypeStatement: 'statement 1' },
			{ id: 72, internal: false, functionalImpacts: [{ name: 'hypermorph' }], phenotypeStatement: 'statement 2' },
		],
	},
	{
		path: 'inheritancemodes',
		label: 'inheritance modes',
		title: 'Inheritance Modes',
		errorField: 'alleleInheritanceModes',
		rows: [
			{ id: 81, internal: false, inheritanceMode: { name: 'dominant' }, phenotypeStatement: 'statement 1' },
			{ id: 82, internal: false, inheritanceMode: { name: 'recessive' }, phenotypeStatement: 'statement 2' },
		],
	},
	{
		path: 'relatednotes',
		label: 'related notes',
		title: 'Related Notes',
		errorField: 'relatedNotes',
		rows: [
			{ id: 91, internal: false, freeText: 'note 1', noteType: { name: 'comment' } },
			{ id: 92, internal: false, freeText: 'note 2', noteType: { name: 'comment' } },
		],
	},
];

vi.mock('../../../service/AlleleService', async () =>
	(await import('./alleleServiceMocks')).mockAlleleService({ getAllele, saveAlleleDetail })
);
vi.mock('../../../service/AlleleSubResourceService', async () => ({
	AlleleSubResourceService: class {
		getList = (alleleId, path) => getRows(path, alleleId);
		replaceList = (alleleId, path, rows) => replaceRows(path, alleleId, rows);
	},
}));
vi.mock('../../../service/SearchService', async () => (await import('./alleleServiceMocks')).mockSearchService());

const AlleleDetailPage = (await import('../AlleleDetailPage')).default;

const renderPage = () => renderInRouter(<AlleleDetailPage />);

const rowsOf = (path) => LISTS.find((list) => list.path === path)?.rows ?? [];

describe.each(LISTS)('<AlleleDetailPage /> $title', { timeout: 60000 }, ({ path, label, title, errorField, rows }) => {
	const replaceCalls = () => replaceRows.mock.calls.filter(([calledPath]) => calledPath === path);

	beforeEach(() => {
		window.localStorage.removeItem('AlleleDetailFormSettings');
		getAllele.mockReset();
		saveAlleleDetail.mockReset();
		getRows.mockReset();
		replaceRows.mockReset();
		getAllele.mockResolvedValue({ data: { entity: storedAllele } });
		saveAlleleDetail.mockResolvedValue({ data: { entity: storedAllele } });
		getRows.mockImplementation((listPath) => Promise.resolve({ data: { entities: rowsOf(listPath) } }));
		replaceRows.mockImplementation((listPath, alleleId, sentRows) =>
			Promise.resolve({ data: { entities: sentRows.map((row, index) => ({ ...row, id: row.id ?? 900 + index })) } })
		);
	});

	// A section, from its title down to its table.
	const sectionOf = (name) => screen.getByRole('heading', { name }).closest('.grid').parentElement;
	const tableRows = () => within(sectionOf(title)).getAllByRole('row').slice(1);

	// The page reads each list once the allele has loaded, and refuses to save it until that read is done, so the
	// tests wait for it as a curator would.
	const waitForRowsToLoad = async () => {
		await waitFor(() => expect(getRows).toHaveBeenCalledWith(path, storedAllele.id), FORM_LOAD_WAIT);
		await act(async () => {
			await new Promise((resolve) => setTimeout(resolve, 0));
		});
		await waitFor(() => expect(tableRows()).toHaveLength(2), FORM_LOAD_WAIT);
	};

	const deleteFirstRow = async (user) => {
		await waitForRowsToLoad();
		await user.click(tableRows()[0].querySelector('.pi-trash').closest('button'));
	};

	it('Reads the rows through their own sub-resource, with nothing pending', async () => {
		await renderPage();
		await waitForRowsToLoad();

		expect(screen.getByRole('button', { name: `Save ${title}` })).toBeDisabled();
		expect(within(sectionOf(title)).queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Marks edited rows as pending and saves them alone, leaving other pending edits marked', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstRow(user);
		await markAlleleExtinct(user);
		expect(within(sectionOf(title)).getByText('Pending Edits!')).toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Save' })).toBeEnabled();

		await user.click(screen.getByRole('button', { name: `Save ${title}` }));

		expect(await screen.findByText(`${title} Saved`)).toBeInTheDocument();
		expect(replaceCalls()).toEqual([[path, storedAllele.id, [rows[1]]]]);
		expect(saveAlleleDetail).not.toHaveBeenCalled();
		expect(within(sectionOf(title)).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(within(sectionOf('Is Extinct')).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('Saves edited rows along with the allele, without sending them in the allele', async () => {
		const user = userEvent.setup();
		await renderPage();

		await deleteFirstRow(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceCalls()).toEqual([[path, storedAllele.id, [rows[1]]]]);
		expect(replaceRows.mock.calls.every(([calledPath]) => calledPath === path)).toBe(true);
		expect(saveAlleleDetail.mock.calls[0][0]).not.toHaveProperty(errorField);
	});

	// The text editors report their row by its index, which the form maps to the row held there.
	it.runIf(path === 'synonyms')('Saves a text edit on the row it was made in', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForRowsToLoad();

		const displayText = getCellByHeader(sectionOf(title), 'Display Text', 1).querySelector('input');
		await user.type(displayText, 'x');
		expect(within(sectionOf(title)).getByText('Pending Edits!')).toBeInTheDocument();

		await user.click(screen.getByRole('button', { name: `Save ${title}` }));
		expect(await screen.findByText(`${title} Saved`)).toBeInTheDocument();
		expect(replaceCalls()).toEqual([[path, storedAllele.id, [rows[0], { ...rows[1], displayText: 'syn-2x' }]]]);
	});

	it.runIf(path === 'synonyms')('Clears the mark when a text edit is undone', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForRowsToLoad();

		const displayText = getCellByHeader(sectionOf(title), 'Display Text', 1).querySelector('input');
		await user.type(displayText, 'x');
		expect(within(sectionOf(title)).getByText('Pending Edits!')).toBeInTheDocument();
		await user.type(displayText, '{Backspace}');

		expect(within(sectionOf(title)).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: `Save ${title}` })).toBeDisabled();
	});

	it('Leaves untouched rows alone when the allele is saved', async () => {
		const user = userEvent.setup();
		await renderPage();
		await waitForRowsToLoad();

		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Allele Saved')).toBeInTheDocument();
		expect(replaceRows).not.toHaveBeenCalled();
	});

	it('Marks the rejected row by its index and keeps the section pending', async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: `Could not update ${title}`,
					supplementalData: { errorMap: { [errorField]: { 0: { internal: 'Not a valid entry' } } } },
				},
			},
		});
		await renderPage();

		await deleteFirstRow(user);
		await user.click(screen.getByRole('button', { name: `Save ${title}` }));

		expect(await screen.findByText(`Could not update ${title}`, OUTSIDE_SAVE_PROGRESS)).toBeInTheDocument();
		expect(within(tableRows()[0]).getByText('Not a valid entry')).toBeInTheDocument();
		expect(within(sectionOf(title)).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('Says the rows were saved when only the allele is rejected', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		await renderPage();

		await deleteFirstRow(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText(`Could not update Allele. The ${label} were saved.`)).toBeInTheDocument();
	});
});
