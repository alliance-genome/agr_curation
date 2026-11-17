import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const {
	getTransgenicTool,
	createTransgenicTool,
	saveTransgenicTool,
	getTransgenicToolAssociations,
	replaceTransgenicToolAssociations,
} = vi.hoisted(() => ({
	getTransgenicTool: vi.fn(),
	createTransgenicTool: vi.fn(),
	saveTransgenicTool: vi.fn(),
	getTransgenicToolAssociations: vi.fn(),
	replaceTransgenicToolAssociations: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/TransgenicToolService', () => ({
	TransgenicToolService: class {
		getTransgenicTool = getTransgenicTool;
		createTransgenicTool = createTransgenicTool;
		saveTransgenicTool = saveTransgenicTool;
		getTransgenicToolAssociations = getTransgenicToolAssociations;
		replaceTransgenicToolAssociations = replaceTransgenicToolAssociations;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const TransgenicToolCreatePage = (await import('../TransgenicToolCreatePage')).default;

// each wait follows stubbed calls through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

// The new tool, as POST /transgenic-tool returns it with its minted curie
const created = (payload, id = 99) => ({ ...payload, id, curie: `AGRKB:1220000000000${id}` });

const nonUnique = {
	response: {
		status: 400,
		statusText: 'Bad Request',
		data: { errorMessage: 'Could not create TransgenicTool', errorMessages: { uniqueId: 'Non-unique value' } },
	},
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={['/transgenic-tool/create']}>
			<Routes>
				<Route path="/transgenic-tool/create" element={<TransgenicToolCreatePage />} />
				<Route path="/transgenicTools" element={<div>Transgenic Tools table</div>} />
			</Routes>
		</MemoryRouter>
	);

const renderReadyPage = async () => {
	renderPage();
	await screen.findByRole('heading', { name: 'Add Transgenic Tool' });
};

// The page's own buttons; a table's filter also has a Clear button
const pageButton = (name) =>
	screen.getAllByRole('button', { name }).find((button) => button.closest('.p-splitter')) ??
	screen.getByRole('button', { name });

const click = (user, name) => user.click(pageButton(name));

// The rows of the associations table: the nearest table to its Add Association button
const associationRows = () => {
	let section = screen.getByRole('button', { name: 'Add Association' }).parentElement;
	while (section && !section.querySelector('.p-datatable')) section = section.parentElement;
	return section ? [...section.querySelectorAll('.p-datatable-tbody tr')] : [];
};

const addAssociation = async (user) => {
	await user.click(screen.getByRole('button', { name: 'Add Association' }));
};

// Sets a yes/no field from its dropdown, as a curator would.
const chooseBoolean = async (user, fieldName, value) => {
	const row = screen.getByRole('heading', { name: fieldName }).closest('.grid');
	await user.click(row.querySelector('.p-dropdown'));
	const panel = await waitFor(() => {
		const openPanel = document.querySelector('.p-dropdown-panel');
		expect(openPanel).not.toBeNull();
		return openPanel;
	});
	await user.click(within(panel).getByText(value));
};

describe('<TransgenicToolCreatePage />', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('TransgenicToolCreateFormSettings');
		[
			getTransgenicTool,
			createTransgenicTool,
			saveTransgenicTool,
			getTransgenicToolAssociations,
			replaceTransgenicToolAssociations,
		].forEach((mock) => mock.mockReset());
		let nextId = 99;
		createTransgenicTool.mockImplementation((payload) =>
			Promise.resolve({ data: { entity: created(payload, nextId++) } })
		);
		saveTransgenicTool.mockImplementation((payload) => Promise.resolve({ data: { entity: payload } }));
		replaceTransgenicToolAssociations.mockImplementation((id, rows) =>
			Promise.resolve({ data: { entities: rows.map((row, index) => ({ ...row, id: 500 + index })) } })
		);
	});

	it("shows the ticket's fields, without those the API sets, and the page's buttons", async () => {
		await renderReadyPage();

		[
			'Full Name',
			'Symbol',
			'Synonyms',
			'Secondary IDs',
			'Transgenic Tool Uses',
			'Transgenic Tool Associations',
			'Related Notes',
			'Cross References',
			'References',
			'Placeholder',
			'Internal',
		].forEach((field) => expect(screen.getAllByText(field).length).toBeGreaterThan(0));
		['Unique ID', 'Curie', 'Primary External ID', 'Data Provider', 'Date Created', 'Obsolete'].forEach((field) =>
			expect(screen.queryByText(field)).not.toBeInTheDocument()
		);
		['Clear', 'Cancel', 'Save & Close', 'Save & Add Another'].forEach((name) =>
			expect(screen.getByRole('button', { name })).toBeInTheDocument()
		);
		expect(screen.getByRole('button', { name: 'Show all fields' })).toBeInTheDocument();
		// a new tool has nothing stored to read
		expect(getTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('creates the tool without associations, then goes back to the Transgenic Tools table', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await chooseBoolean(user, 'Internal', 'true');
		await click(user, 'Save & Close');

		expect(await screen.findByText('Transgenic Tools table', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		const [payload] = createTransgenicTool.mock.calls[0];
		expect(payload).toMatchObject({ type: 'TransgenicTool', placeholder: false, internal: true });
		['id', 'dataProvider', 'associatedTransgenicTools'].forEach((field) => expect(payload).not.toHaveProperty(field));
		// no association was added, so none is written
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('writes the associations to the new tool once it is created', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await addAssociation(user);
		await click(user, 'Save & Close');

		expect(await screen.findByText('Transgenic Tools table', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(createTransgenicTool).toHaveBeenCalledTimes(1);
		expect(createTransgenicTool.mock.calls[0][0]).not.toHaveProperty('transgenicToolTransgenicToolAssociations');
		expect(replaceTransgenicToolAssociations).toHaveBeenCalledTimes(1);
		const [id, rows] = replaceTransgenicToolAssociations.mock.calls[0];
		expect(id).toBe(99);
		expect(rows).toHaveLength(1);
		expect(rows[0]).not.toHaveProperty('dataKey');
	});

	it('keeps the saved values and associations for the next tool on Save & Add Another', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await chooseBoolean(user, 'Internal', 'true');
		await addAssociation(user);
		await click(user, 'Save & Add Another');
		await waitFor(() => expect(replaceTransgenicToolAssociations).toHaveBeenCalledTimes(1), PAGE_LOAD_WAIT);
		await waitFor(() => expect(screen.getByRole('button', { name: 'Save & Add Another' })).toBeEnabled());

		expect(screen.getByRole('heading', { name: 'Add Transgenic Tool' })).toBeInTheDocument();
		await click(user, 'Save & Add Another');
		await waitFor(() => expect(replaceTransgenicToolAssociations).toHaveBeenCalledTimes(2), PAGE_LOAD_WAIT);

		// a second tool is created, not the first updated, and the associations are written to it as new rows
		expect(createTransgenicTool).toHaveBeenCalledTimes(2);
		expect(saveTransgenicTool).not.toHaveBeenCalled();
		const next = createTransgenicTool.mock.calls[1][0];
		expect(next).not.toHaveProperty('id');
		expect(next).not.toHaveProperty('curie');
		expect(next.internal).toBe(true);
		const [id, rows] = replaceTransgenicToolAssociations.mock.calls[1];
		expect(id).toBe(100);
		expect(rows).toHaveLength(1);
		expect(rows[0]).not.toHaveProperty('id');
	});

	it('keeps a created tool whose associations failed, and saving again updates it', async () => {
		const user = userEvent.setup();
		replaceTransgenicToolAssociations.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update associations' } },
		});
		await renderReadyPage();

		await addAssociation(user);
		await click(user, 'Save & Close');

		expect(await screen.findByText(/AGRKB:122000000000099 was created/, {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(screen.getByRole('heading', { name: 'Add Transgenic Tool' })).toBeInTheDocument();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save & Close' })).toBeEnabled());
		await click(user, 'Save & Close');

		expect(await screen.findByText('Transgenic Tools table', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(createTransgenicTool).toHaveBeenCalledTimes(1);
		expect(saveTransgenicTool).toHaveBeenCalledWith(expect.objectContaining({ id: 99, type: 'TransgenicTool' }));
		expect(replaceTransgenicToolAssociations.mock.calls.map(([id]) => id)).toEqual([99, 99]);
	});

	it('says so when a tool with the same symbol and full name exists', async () => {
		const user = userEvent.setup();
		createTransgenicTool.mockRejectedValue(nonUnique);
		await renderReadyPage();

		await addAssociation(user);
		await click(user, 'Save & Close');

		expect(await screen.findByText(/symbol and full name already exists/)).toBeInTheDocument();
		expect(screen.getByText('Could not create TransgenicTool')).toBeInTheDocument();
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('refuses, before saving anything, an association whose tool was typed rather than picked', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await addAssociation(user);
		const [row] = associationRows();
		await user.type(row.querySelector('.p-autocomplete-input'), 'UAS');
		await click(user, 'Save & Close');

		expect(await screen.findByText('Select each tool from the autosuggest')).toBeInTheDocument();
		expect(createTransgenicTool).not.toHaveBeenCalled();
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('empties the form and the associations on Clear', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await addAssociation(user);
		expect(associationRows()).toHaveLength(1);
		await click(user, 'Clear');
		await click(user, 'Save & Close');

		expect(await screen.findByText('Transgenic Tools table', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('goes back to the Transgenic Tools table on Cancel', async () => {
		const user = userEvent.setup();
		await renderReadyPage();

		await click(user, 'Cancel');

		expect(await screen.findByText('Transgenic Tools table')).toBeInTheDocument();
	});
});
