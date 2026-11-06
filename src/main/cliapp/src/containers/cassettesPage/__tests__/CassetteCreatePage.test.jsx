import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const { getCassette, saveCassette, createCassette, getComponentList, replaceComponentList } = vi.hoisted(() => ({
	getCassette: vi.fn(),
	saveCassette: vi.fn(),
	createCassette: vi.fn(),
	getComponentList: vi.fn(),
	replaceComponentList: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/CassetteService', () => ({
	CassetteService: class {
		getCassette = getCassette;
		saveCassette = saveCassette;
		createCassette = createCassette;
		getComponentList = getComponentList;
		replaceComponentList = replaceComponentList;
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

const CassetteCreatePage = (await import('../CassetteCreatePage')).default;

// each wait follows stubbed calls through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

// The cassette Clone copies from, as GET /cassette/{identifier} returns it
const source = {
	type: 'Cassette',
	id: 11,
	uniqueId: 'FB:FBcs0000001',
	curie: 'AGRKB:123000000000001',
	primaryExternalId: 'FB:FBcs0000001',
	cassetteSymbol: { id: 5, displayText: 'UAS-GFP', formatText: 'UAS-GFP' },
	cassetteFullName: null,
	cassetteSynonyms: [{ id: 1, displayText: 'UAS-EGFP', formatText: 'UAS-<sup>EGFP</sup>' }],
	secondaryIdentifiers: ['FB:FBcs9000001'],
	cassetteUses: [{ id: 2, uses: [{ id: 70, curie: 'FBcv:0005012', name: 'driver' }], evidence: [] }],
	references: [{ id: 80, curie: 'AGRKB:101000000001', crossReferences: [] }],
	relatedNotes: [{ id: 3, freeText: 'A reporter.', noteType: { name: 'comment' } }],
	dataProvider: { abbreviation: 'FB' },
	placeholder: false,
	internal: true,
	obsolete: false,
};

const sourceComponent = {
	id: 41,
	relation: { id: 51, name: 'tagged_with' },
	componentSymbol: 'GFP',
	taxonText: 'Aequorea victoria',
	relatedNotes: [{ id: 61, freeText: 'From jellyfish.', noteType: { name: 'comment' } }],
	singleCassette: { id: 11 },
	internal: false,
	obsolete: false,
};

// The new cassette, as POST /cassette returns it with its minted curie
const created = (payload) => ({ ...payload, id: 99, curie: 'AGRKB:123000000000099' });

const renderPage = (entry = '/cassette/create') =>
	renderWithClient(
		<MemoryRouter initialEntries={[entry]}>
			<Routes>
				<Route path="/cassette/create" element={<CassetteCreatePage />} />
				<Route path="/cassette/:identifier" element={<div>Detail page</div>} />
				<Route path="/cassettes" element={<div>Cassettes table</div>} />
			</Routes>
		</MemoryRouter>
	);

const renderClone = async () => {
	renderPage('/cassette/create?from=FB%3AFBcs0000001');
	await screen.findByDisplayValue('Aequorea victoria', {}, PAGE_LOAD_WAIT);
};

// The page's own buttons; a table's filter also has a Clear button
const pageButton = (name) =>
	screen.getAllByRole('button', { name }).find((button) => button.closest('.p-splitter')) ??
	screen.getByRole('button', { name });

const click = (user, name) => user.click(pageButton(name));

describe('<CassetteCreatePage />', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('CassetteCreateFormSettings');
		[getCassette, saveCassette, createCassette, getComponentList, replaceComponentList].forEach((mock) =>
			mock.mockReset()
		);
		getCassette.mockResolvedValue({ data: { entity: source } });
		getComponentList.mockImplementation((id, list) =>
			Promise.resolve({ data: { entities: list === 'components' ? [sourceComponent] : [] } })
		);
		createCassette.mockImplementation((payload) => Promise.resolve({ data: { entity: created(payload) } }));
		saveCassette.mockImplementation((payload) => Promise.resolve({ data: { entity: payload } }));
		replaceComponentList.mockImplementation((id, list, rows) =>
			Promise.resolve({ data: { entities: rows.map((row, index) => ({ ...row, id: 500 + index })) } })
		);
	});

	it("shows the ticket's fields, without those the API sets, and the page's buttons", async () => {
		renderPage();

		expect(await screen.findByRole('heading', { name: 'Add Cassette' })).toBeInTheDocument();
		[
			'Symbol',
			'Name',
			'Synonyms',
			'Secondary IDs',
			'Free Text Components',
			'Genomic Entity Components',
			'Transgenic Tool Components',
			'Sequence Targeting Reagent Components',
			'Uses',
			'References',
			'Placeholder',
			'Internal',
		].forEach((field) => expect(screen.getAllByText(field).length).toBeGreaterThan(0));
		['Unique ID', 'Curie', 'Data Provider', 'Date Created', 'Obsolete'].forEach((field) =>
			expect(screen.queryByText(field)).not.toBeInTheDocument()
		);
		['Clear', 'Cancel', 'Save & Close', 'Save & Add Another'].forEach((name) =>
			expect(screen.getByRole('button', { name })).toBeInTheDocument()
		);
	});

	it('creates the cassette, then lands on its detail page', async () => {
		const user = userEvent.setup();
		renderPage();
		await screen.findByRole('heading', { name: 'Add Cassette' });

		await click(user, 'Save & Close');

		expect(await screen.findByText('Detail page', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		const [payload] = createCassette.mock.calls[0];
		expect(payload).toMatchObject({ type: 'Cassette', placeholder: false, internal: false });
		expect(payload).not.toHaveProperty('id');
		expect(payload).not.toHaveProperty('cassetteComponents');
		expect(payload).not.toHaveProperty('dataProvider');
		// no component list was filled in, so none is written
		expect(replaceComponentList).not.toHaveBeenCalled();
	});

	it("clones a cassette's fields and component lists, without what identifies it", async () => {
		const user = userEvent.setup();
		await renderClone();

		expect(getCassette).toHaveBeenCalledWith('FB:FBcs0000001');
		expect(getComponentList).toHaveBeenCalledWith(11, 'components');
		expect(screen.getByDisplayValue('UAS-EGFP')).toBeInTheDocument();
		expect(screen.queryByDisplayValue('FB:FBcs9000001')).not.toBeInTheDocument();

		await click(user, 'Save & Close');

		await waitFor(() => expect(replaceComponentList).toHaveBeenCalledTimes(1), PAGE_LOAD_WAIT);
		const [payload] = createCassette.mock.calls[0];
		['id', 'curie', 'uniqueId', 'primaryExternalId', 'secondaryIdentifiers', 'dataProvider', 'relatedNotes'].forEach(
			(field) => expect(payload).not.toHaveProperty(field)
		);
		expect(payload.cassetteSymbol).toEqual({ displayText: 'UAS-GFP', formatText: 'UAS-GFP' });
		expect(payload.cassetteSynonyms).toEqual([{ displayText: 'UAS-EGFP', formatText: 'UAS-<sup>EGFP</sup>' }]);
		expect(payload.cassetteUses).toEqual([{ uses: [{ id: 70, curie: 'FBcv:0005012', name: 'driver' }], evidence: [] }]);
		expect(payload.references).toEqual([{ type: 'Reference', curie: 'AGRKB:101000000001' }]);
		expect(payload.internal).toBe(true);

		const [cassetteId, list, rows] = replaceComponentList.mock.calls[0];
		expect([cassetteId, list]).toEqual([99, 'components']);
		expect(rows).toEqual([
			{
				relation: { id: 51, name: 'tagged_with' },
				componentSymbol: 'GFP',
				taxonText: 'Aequorea victoria',
				relatedNotes: [{ freeText: 'From jellyfish.', noteType: { name: 'comment' } }],
				internal: false,
				obsolete: false,
			},
		]);
		expect(await screen.findByText('Detail page', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
	});

	it('keeps the saved values for the next cassette on Save & Add Another', async () => {
		const user = userEvent.setup();
		await renderClone();

		await click(user, 'Save & Add Another');
		await waitFor(() => expect(replaceComponentList).toHaveBeenCalledTimes(1), PAGE_LOAD_WAIT);
		await waitFor(() => expect(screen.getByRole('button', { name: 'Save & Add Another' })).toBeEnabled());

		expect(screen.getByRole('heading', { name: 'Add Cassette' })).toBeInTheDocument();
		expect(screen.getByDisplayValue('UAS-EGFP')).toBeInTheDocument();
		expect(screen.getByDisplayValue('Aequorea victoria')).toBeInTheDocument();

		await click(user, 'Save & Add Another');
		await waitFor(() => expect(replaceComponentList).toHaveBeenCalledTimes(2), PAGE_LOAD_WAIT);

		// a second cassette is created, not the first updated, and its rows are new ones
		expect(createCassette).toHaveBeenCalledTimes(2);
		expect(saveCassette).not.toHaveBeenCalled();
		expect(createCassette.mock.calls[1][0]).not.toHaveProperty('id');
		expect(replaceComponentList.mock.calls[1][2][0]).not.toHaveProperty('id');
	});

	it('keeps a created cassette whose component list failed, and saving again updates it', async () => {
		const user = userEvent.setup();
		replaceComponentList.mockRejectedValueOnce({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Could not update components' } },
		});
		await renderClone();

		await click(user, 'Save & Close');

		expect(await screen.findByText(/AGRKB:123000000000099 was created/, {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(screen.getByRole('heading', { name: 'Add Cassette' })).toBeInTheDocument();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save & Close' })).toBeEnabled());
		await click(user, 'Save & Close');

		await waitFor(() => expect(replaceComponentList).toHaveBeenCalledTimes(2), PAGE_LOAD_WAIT);
		expect(createCassette).toHaveBeenCalledTimes(1);
		expect(saveCassette).toHaveBeenCalledWith(expect.objectContaining({ id: 99 }));
		expect(replaceComponentList.mock.calls[1][0]).toBe(99);
		expect(await screen.findByText('Detail page', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
	});

	it('says so when a cassette with the same unique ID exists', async () => {
		const user = userEvent.setup();
		createCassette.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: { errorMessage: 'Could not create Cassette', errorMessages: { uniqueId: 'Non-unique value' } },
			},
		});
		renderPage();
		await screen.findByRole('heading', { name: 'Add Cassette' });

		await click(user, 'Save & Close');

		expect(await screen.findByText(/symbol, name and free text components already exists/)).toBeInTheDocument();
		expect(screen.getByText('Could not create Cassette')).toBeInTheDocument();
		expect(replaceComponentList).not.toHaveBeenCalled();
	});

	it('empties the form and the component lists on Clear', async () => {
		const user = userEvent.setup();
		await renderClone();

		await click(user, 'Clear');

		await waitFor(() => expect(screen.queryByDisplayValue('Aequorea victoria')).not.toBeInTheDocument());
		expect(screen.queryByDisplayValue('UAS-EGFP')).not.toBeInTheDocument();
	});

	it('goes back to the Cassettes table on Cancel', async () => {
		const user = userEvent.setup();
		renderPage();
		await screen.findByRole('heading', { name: 'Add Cassette' });

		await click(user, 'Cancel');

		expect(await screen.findByText('Cassettes table')).toBeInTheDocument();
	});

	it('reports a cassette to clone that cannot be loaded, and starts blank', async () => {
		getCassette.mockRejectedValue(new Error('not found'));
		renderPage('/cassette/create?from=FB%3AFBcs0000404');

		expect(
			await screen.findByText('Could not load cassette FB:FBcs0000404 to clone', {}, PAGE_LOAD_WAIT)
		).toBeInTheDocument();
		expect(getComponentList).not.toHaveBeenCalled();
	});
});
