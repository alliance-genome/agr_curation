import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const { getTransgenicTool, saveTransgenicTool, getTransgenicToolAssociations, replaceTransgenicToolAssociations } =
	vi.hoisted(() => ({
		getTransgenicTool: vi.fn(),
		saveTransgenicTool: vi.fn(),
		getTransgenicToolAssociations: vi.fn(),
		replaceTransgenicToolAssociations: vi.fn(),
	}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/TransgenicToolService', () => ({
	TransgenicToolService: class {
		getTransgenicTool = getTransgenicTool;
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

const TransgenicToolDetailPage = (await import('../TransgenicToolDetailPage')).default;

// each wait follows a stubbed call through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

const tool = {
	id: 11,
	uniqueId: 'FB:FBto0000393',
	curie: 'AGRKB:122000000000393',
	primaryExternalId: 'FB:FBto0000393',
	transgenicToolSymbol: { id: 5, displayText: 'GAL4', formatText: 'GAL4' },
	transgenicToolFullName: {
		id: 6,
		displayText: 'GAL4 transcription factor',
		formatText: 'GAL4 <i>transcription</i> factor',
		nameType: { id: 7, name: 'full_name_type_beta' },
	},
	transgenicToolSynonyms: [{ id: 1, displayText: 'Gal4p', formatText: 'Gal4<sup>p</sup>' }],
	secondaryIdentifiers: ['FB:FBto9000393'],
	transgenicToolUses: [{ id: 2, uses: [{ id: 70, curie: 'FBcv:0005012', name: 'driver' }], evidence: [] }],
	references: [{ id: 80, curie: 'AGRKB:101000000001', crossReferences: [] }],
	relatedNotes: [{ id: 3, freeText: 'A yeast transcription factor.', noteType: { name: 'summary' } }],
	crossReferences: [],
	dataProvider: { abbreviation: 'FB' },
	placeholder: false,
	internal: false,
	obsolete: false,
	associatedTransgenicTools: [{ relation: 'is_compatible_with', symbol: 'UAS', identifier: 'FB:FBto0000135' }],
};

const association = {
	id: 21,
	relation: { id: 31, name: 'is_compatible_with' },
	transgenicToolTransgenicToolAssociationObject: {
		id: 12,
		primaryExternalId: 'FB:FBto0000135',
		transgenicToolSymbol: { displayText: 'UAS' },
	},
	internal: false,
	obsolete: false,
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={['/transgenic-tool/FB:FBto0000393']}>
			<Routes>
				<Route path="/transgenic-tool/:identifier" element={<TransgenicToolDetailPage />} />
			</Routes>
		</MemoryRouter>
	);

// The page reads the tool's associations once the tool has loaded, and refuses to save them until that read
// is done, so the tests wait for it as a curator would.
const waitForAssociationsToLoad = async () => {
	await waitFor(() => expect(getTransgenicToolAssociations).toHaveBeenCalledWith(11), PAGE_LOAD_WAIT);
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

describe('<TransgenicToolDetailPage />', { timeout: 30000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('TransgenicToolDetailFormSettings');
		[getTransgenicTool, saveTransgenicTool, getTransgenicToolAssociations, replaceTransgenicToolAssociations].forEach(
			(mock) => mock.mockReset()
		);
		getTransgenicTool.mockResolvedValue({ data: { entity: tool } });
		saveTransgenicTool.mockImplementation((payload) => Promise.resolve({ data: { entity: payload } }));
		getTransgenicToolAssociations.mockResolvedValue({ data: { entities: [association] } });
		replaceTransgenicToolAssociations.mockResolvedValue({ data: { entities: [] } });
	});

	it('loads the tool by the identifier in the URL and shows its fields for editing', async () => {
		renderPage();

		expect(await screen.findByDisplayValue('GAL4 transcription factor', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(getTransgenicTool).toHaveBeenCalledWith('FB:FBto0000393');
		expect(screen.getByDisplayValue('Gal4p')).toBeInTheDocument();
		expect(screen.getByDisplayValue('FB:FBto9000393')).toBeInTheDocument();
		expect(screen.getByText('driver (FBcv:0005012)')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:101000000001')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:122000000000393')).toBeInTheDocument();
	});

	it("reads the tool's associations through their own endpoint", async () => {
		renderPage();
		await waitForAssociationsToLoad();

		expect(await screen.findByDisplayValue('UAS (FB:FBto0000135)', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
	});

	it('offers the field menu and Show all fields', async () => {
		renderPage();

		await waitFor(
			() => expect(screen.getByRole('button', { name: 'Show all fields' })).toBeInTheDocument(),
			PAGE_LOAD_WAIT
		);
	});

	it('saves an edited field with the tool, leaving the associations alone', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForAssociationsToLoad();

		await waitFor(() => expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled());
		await chooseBoolean(user, 'Placeholder', 'true');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Transgenic Tool Saved')).toBeInTheDocument();
		const payload = saveTransgenicTool.mock.calls[0][0];
		expect(payload.placeholder).toBe(true);
		expect(payload.references).toEqual([{ type: 'Reference', curie: 'AGRKB:101000000001' }]);
		expect(payload.transgenicToolSynonyms[0]).not.toHaveProperty('dataKey');
		expect(payload.transgenicToolSymbol).not.toHaveProperty('dataKey');
		expect(payload).not.toHaveProperty('associatedTransgenicTools');
		expect(replaceTransgenicToolAssociations).not.toHaveBeenCalled();
	});

	it('saves a deleted association through its own endpoint, without saving the tool', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForAssociationsToLoad();

		const row = (await screen.findByDisplayValue('UAS (FB:FBto0000135)', {}, PAGE_LOAD_WAIT)).closest('tr');
		await user.click(row.querySelector('.pi-trash').closest('button'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Transgenic Tool Saved')).toBeInTheDocument();
		expect(replaceTransgenicToolAssociations).toHaveBeenCalledWith(11, []);
		expect(saveTransgenicTool).not.toHaveBeenCalled();
	});

	it('saves a new related note with the note type chosen for it', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForAssociationsToLoad();

		await user.click(screen.getByRole('button', { name: 'Add Related Note' }));
		const notesTable = screen.getByDisplayValue('A yeast transcription factor.').closest('tbody');
		const newRow = notesTable.querySelectorAll('tr')[0];
		await user.click(newRow.querySelector('.p-dropdown'));
		const panel = await waitFor(() => {
			const openPanel = document.querySelector('.p-dropdown-panel');
			expect(openPanel).not.toBeNull();
			return openPanel;
		});
		await user.click(within(panel).getByText('transgenic_tool_note_type_beta'));
		await user.type(newRow.querySelector('textarea'), 'A new note');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Transgenic Tool Saved')).toBeInTheDocument();
		const payload = saveTransgenicTool.mock.calls[0][0];
		expect(payload.relatedNotes).toHaveLength(2);
		expect(payload.relatedNotes[0]).toMatchObject({
			freeText: 'A new note',
			noteType: { name: 'transgenic_tool_note_type_beta' },
		});
	});

	it('saves the name type chosen for the full name', async () => {
		const user = userEvent.setup();
		renderPage();
		await waitForAssociationsToLoad();

		const fullNameRow = screen.getByDisplayValue('GAL4 transcription factor').closest('tr');
		await user.click(fullNameRow.querySelector('[aria-label="nameType"]').closest('.p-dropdown'));
		const panel = await waitFor(() => {
			const openPanel = document.querySelector('.p-dropdown-panel');
			expect(openPanel).not.toBeNull();
			return openPanel;
		});
		await user.click(within(panel).getByText('full_name_type_alpha'));
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Transgenic Tool Saved')).toBeInTheDocument();
		const payload = saveTransgenicTool.mock.calls[0][0];
		expect(payload.transgenicToolFullName.nameType).toMatchObject({ name: 'full_name_type_alpha' });
	});

	it('reports a rejected tool', async () => {
		const user = userEvent.setup();
		saveTransgenicTool.mockRejectedValue({
			response: {
				status: 400,
				statusText: 'Bad Request',
				data: { errorMessage: 'Could not update TransgenicTool: [11]', errorMessages: {} },
			},
		});
		renderPage();
		await waitForAssociationsToLoad();

		await chooseBoolean(user, 'Internal', 'true');
		await user.click(screen.getByRole('button', { name: 'Save' }));

		expect(await screen.findByText('Could not update TransgenicTool: [11]')).toBeInTheDocument();
	});
});
