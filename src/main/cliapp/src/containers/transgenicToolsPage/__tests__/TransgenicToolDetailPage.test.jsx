import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { Endpoints } from '../../../constants/Endpoints';

const getTransgenicTool = vi.fn();
const find = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/TransgenicToolService', () => ({
	TransgenicToolService: class {
		getTransgenicTool = getTransgenicTool;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		find = find;
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const TransgenicToolDetailPage = (await import('../TransgenicToolDetailPage')).default;

// each wait follows a stubbed call through a full render of the page, which can outlast the default under load
const PAGE_LOAD_WAIT = { timeout: 5000 };

const tool = {
	id: 11,
	uniqueId: 'FB:FBto0000393',
	curie: 'AGRKB:122000000000393',
	primaryExternalId: 'FB:FBto0000393',
	transgenicToolSymbol: { displayText: 'GAL4', formatText: 'GAL4' },
	transgenicToolFullName: { displayText: 'GAL4 transcription factor', formatText: 'GAL4 <i>transcription factor</i>' },
	transgenicToolSynonyms: [{ id: 1, displayText: 'Gal4p', formatText: 'Gal4<sup>p</sup>' }],
	secondaryIdentifiers: ['FB:FBto9000393'],
	transgenicToolUses: [{ id: 2, uses: [{ curie: 'FBcv:0005012', name: 'driver' }], evidence: [] }],
	references: [],
	relatedNotes: [{ id: 3, freeText: 'A yeast transcription factor.', noteType: { name: 'summary' } }],
	crossReferences: [
		{ id: 4, referencedCurie: 'FB:FBto0000393', displayName: 'GAL4', resourceDescriptorPage: { name: 'default' } },
	],
	dataProvider: { abbreviation: 'FB' },
	placeholder: false,
	internal: false,
	obsolete: false,
};

const association = {
	id: 21,
	relation: { name: 'is_compatible_with' },
	transgenicToolTransgenicToolAssociationObject: {
		primaryExternalId: 'FB:FBto0000135',
		transgenicToolSymbol: { displayText: 'UAS' },
	},
};

const renderPage = () => {
	getTransgenicTool.mockResolvedValue({ data: { entity: tool } });
	find.mockResolvedValue({ results: [association], totalResults: 1 });
	renderWithClient(
		<MemoryRouter initialEntries={['/transgenic-tool/FB:FBto0000393']}>
			<Routes>
				<Route path="/transgenic-tool/:identifier" element={<TransgenicToolDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
};

describe('<TransgenicToolDetailPage />', () => {
	beforeEach(() => {
		getTransgenicTool.mockReset();
		find.mockReset();
	});

	it('loads the tool by the identifier in the URL and shows its fields', async () => {
		renderPage();

		expect(await screen.findByText('GAL4 transcription factor', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(getTransgenicTool).toHaveBeenCalledWith('FB:FBto0000393');
		expect(screen.getByText('Gal4p')).toBeInTheDocument();
		expect(screen.getByText('FB:FBto9000393')).toBeInTheDocument();
		expect(screen.getByText('driver (FBcv:0005012)')).toBeInTheDocument();
		expect(screen.getByText('A yeast transcription factor.')).toBeInTheDocument();
		expect(screen.getByText('AGRKB:122000000000393')).toBeInTheDocument();
	});

	it("shows the tool's associations, fetched for the tool", async () => {
		renderPage();

		expect(await screen.findByText('UAS (FB:FBto0000135)', {}, PAGE_LOAD_WAIT)).toBeInTheDocument();
		expect(screen.getByText('is_compatible_with')).toBeInTheDocument();
		expect(find).toHaveBeenCalledWith(Endpoints.Entity.TRANSGENIC_TOOL_TRANSGENIC_TOOL_ASSOCIATION, 100, 0, {
			'transgenicToolAssociationSubject.id': 11,
		});
	});

	it('offers the field menu and Show all fields', async () => {
		renderPage();

		await waitFor(
			() => expect(screen.getByRole('button', { name: 'Show all fields' })).toBeInTheDocument(),
			PAGE_LOAD_WAIT
		);
	});
});
