import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const { getCassette, saveCassette, getComponentList, replaceComponentList } = vi.hoisted(() => ({
	getCassette: vi.fn(),
	saveCassette: vi.fn(),
	getComponentList: vi.fn(),
	replaceComponentList: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/CassetteService', () => ({
	CassetteService: class {
		getCassette = getCassette;
		saveCassette = saveCassette;
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

const CassetteDetailPage = (await import('../CassetteDetailPage')).default;

// each wait follows a stubbed call through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

const COMPONENT_LISTS = [
	'components',
	'genomic-entity-associations',
	'transgenic-tool-associations',
	'str-associations',
];

const cassette = {
	id: 11,
	uniqueId: 'FB:FBcs0000001',
	curie: 'AGRKB:123000000000001',
	primaryExternalId: 'FB:FBcs0000001',
	cassetteSymbol: { id: 5, displayText: 'UAS-GFP', formatText: 'UAS-GFP' },
	cassetteFullName: {
		id: 6,
		displayText: 'UAS green fluorescent protein',
		formatText: 'UAS <i>green fluorescent</i> protein',
		nameType: { id: 7, name: 'full_name_type_beta' },
	},
	cassetteSynonyms: [{ id: 1, displayText: 'UAS-EGFP', formatText: 'UAS-<sup>EGFP</sup>' }],
	secondaryIdentifiers: ['FB:FBcs9000001'],
	cassetteUses: [{ id: 2, uses: [{ id: 70, curie: 'FBcv:0005012', name: 'driver' }], evidence: [] }],
	references: [{ id: 80, curie: 'AGRKB:101000000001', crossReferences: [] }],
	relatedNotes: [{ id: 3, freeText: 'A reporter.', noteType: { name: 'comment' } }],
	cassetteComponents: [{ id: 41, componentSymbol: 'GFP' }],
	dataProvider: { abbreviation: 'FB' },
	placeholder: false,
	internal: false,
	obsolete: false,
};

const freeTextComponent = {
	id: 41,
	relation: { id: 51, name: 'tagged_with' },
	componentSymbol: 'GFP',
	taxonText: 'Aequorea victoria',
	relatedNotes: [],
	internal: false,
	obsolete: false,
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={['/cassette/FB:FBcs0000001']}>
			<Routes>
				<Route path="/cassette/:identifier" element={<CassetteDetailPage />} />
			</Routes>
		</MemoryRouter>
	);

// The page reads the cassette's component lists once the cassette has loaded, and refuses to save them until that
// read is done, so the tests wait for it as a curator would.
const waitForComponentsToLoad = async () => {
	await waitFor(() => expect(getComponentList).toHaveBeenCalledTimes(COMPONENT_LISTS.length), PAGE_LOAD_WAIT);
	await act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});
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

const deleteFreeTextComponent = async (user) => {
	const row = (await screen.findByDisplayValue('Aequorea victoria', {}, PAGE_LOAD_WAIT)).closest('tr');
	await user.click(row.querySelector('.pi-trash').closest('button'));
};

describe('<CassetteDetailPage />', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('CassetteDetailFormSettings');
		[getCassette, saveCassette, getComponentList, replaceComponentList].forEach((mock) => mock.mockReset());
		getCassette.mockResolvedValue({ data: { entity: cassette } });
		saveCassette.mockImplementation((payload) => Promise.resolve({ data: { entity: payload } }));
		getComponentList.mockImplementation((id, list) =>
			Promise.resolve({ data: { entities: list === 'components' ? [freeTextComponent] : [] } })
		);
		replaceComponentList.mockResolvedValue({ data: { entities: [] } });
	});

	it('loads the cassette by the identifier in the URL and shows its fields for editing', async () => {
		renderPage();

		expect(await screen.findByDisplayValue('UAS green fluorescent protein', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(getCassette).toHaveBeenCalledWith('FB:FBcs0000001');
		expect(screen.getByDisplayValue('UAS-EGFP')).toBeInTheDocument();
		expect(screen.getByDisplayValue('FB:FBcs9000001')).toBeInTheDocument();
		expect(screen.getByText('driver (FBcv:0005012)')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:101000000001')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:123000000000001')).toBeInTheDocument();
	});

	it('reads each component list through its own endpoint', async () => {
		renderPage();
		await waitForComponentsToLoad();

		COMPONENT_LISTS.forEach((list) => expect(getComponentList).toHaveBeenCalledWith(11, list));
		expect(await screen.findByDisplayValue('Aequorea victoria', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		['Free Text Components', 'Genomic Entity Components', 'Transgenic Tool Components'].forEach((title) =>
			expect(screen.getAllByText(title).length).toBeGreaterThan(0)
		);
	});

	it('offers the field menu and Show all fields', async () => {
		renderPage();

		await waitFor(
			() => expect(screen.getByRole('button', { name: 'Show all fields' })).toBeInTheDocument(),
			PAGE_LOAD_WAIT
		);
	});

	it('opens the create page in a new tab, blank or cloning this cassette', async () => {
		const user = userEvent.setup();
		const open = vi.spyOn(window, 'open').mockImplementation(() => null);
		renderPage();
		await screen.findByDisplayValue('UAS green fluorescent protein', {}, PAGE_LOAD_WAIT);

		await user.click(screen.getByRole('button', { name: 'New Cassette' }));
		await user.click(screen.getByRole('button', { name: 'Clone' }));

		expect(open).toHaveBeenNthCalledWith(1, '/cassette/create', '_blank');
		expect(open).toHaveBeenNthCalledWith(2, '/cassette/create?from=AGRKB%3A123000000000001', '_blank');
		open.mockRestore();
	});

	it('saves an edited field with the cassette, leaving the component lists alone', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForComponentsToLoad();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled());
		await chooseBoolean(user, 'Placeholder', 'true');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Cassette Saved')).toBeInTheDocument();
		const payload = saveCassette.mock.calls[0][0];
		expect(payload.placeholder).toBe(true);
		expect(payload.references).toEqual([{ type: 'Reference', curie: 'AGRKB:101000000001' }]);
		expect(payload.cassetteSynonyms[0]).not.toHaveProperty('dataKey');
		expect(payload.cassetteSymbol).not.toHaveProperty('dataKey');
		expect(payload.relatedNotes).toEqual(cassette.relatedNotes);
		expect(payload).not.toHaveProperty('cassetteComponents');
		expect(replaceComponentList).not.toHaveBeenCalled();
	});

	it('saves a deleted component through its list endpoint, without saving the cassette', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForComponentsToLoad();

		await deleteFreeTextComponent(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Cassette Saved')).toBeInTheDocument();
		expect(replaceComponentList).toHaveBeenCalledTimes(1);
		expect(replaceComponentList).toHaveBeenCalledWith(11, 'components', []);
		expect(saveCassette).not.toHaveBeenCalled();
	});

	it('saves the cassette even when a component list is rejected, and reports the list', async () => {
		const user = userEvent.setup();
		replaceComponentList.mockRejectedValue({
			response: { status: 400, statusText: 'Bad Request', data: { errorMessage: 'Relation is not valid' } },
		});
		renderPage();
		await waitForComponentsToLoad();

		await chooseBoolean(user, 'Internal', 'true');
		await deleteFreeTextComponent(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Free Text Components not saved:')).toBeInTheDocument();
		expect(saveCassette).toHaveBeenCalledTimes(1);
		expect(screen.queryByText('Cassette not saved:')).not.toBeInTheDocument();
	});

	it('reports a rejected cassette', async () => {
		const user = userEvent.setup();
		saveCassette.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: { errorMessage: 'Could not update Cassette: [11]', errorMessages: {} },
			},
		});
		renderPage();
		await waitForComponentsToLoad();

		await chooseBoolean(user, 'Obsolete', 'true');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Could not update Cassette: [11]')).toBeInTheDocument();
	});
});
