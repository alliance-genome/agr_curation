import { MemoryRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { getCellByHeader } from '../../allelesPage/__tests__/alleleFormTestUtils';

const { getAGM, createAGM, navigate } = vi.hoisted(() => ({
	getAGM: vi.fn(),
	createAGM: vi.fn(),
	navigate: vi.fn(),
}));

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AffectedGenomicModelService', () => ({
	AffectedGenomicModelService: class {
		getAGM = getAGM;
		createAGM = createAGM;
	},
}));

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

vi.mock('react-router-dom', async (importOriginal) => ({
	...(await importOriginal()),
	useNavigate: () => navigate,
}));

const AffectedGenomicModelCreatePage = (await import('../AffectedGenomicModelCreatePage')).default;

const SOURCE_CURIE = 'AGRKB:108000000000001';

// The AGM to copy, as the API returns it.
const sourceAgm = {
	id: 4242,
	curie: SOURCE_CURIE,
	type: 'AffectedGenomicModel',
	taxon: { id: 9, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	subtype: { id: 3, name: 'strain' },
	internal: false,
	obsolete: false,
	agmFullName: {
		id: 51,
		displayText: 'wild isolate 1',
		formatText: 'wild isolate 1 format',
		nameType: { id: 7, name: 'full_name' },
		synonymUrl: 'https://example.org',
		internal: false,
		evidence: [{ id: 1, curie: 'AGRKB:101' }],
		dateCreated: '2026-10-23T00:00:00Z',
	},
	agmSynonyms: [],
	agmSecondaryIds: [],
};

const renderPage = () =>
	renderWithClient(
		<MemoryRouter initialEntries={[`/agm/create?from=${encodeURIComponent(SOURCE_CURIE)}`]}>
			<AffectedGenomicModelCreatePage />
		</MemoryRouter>
	);

// Required sections prefix their title with a marker.
const nameSection = () =>
	screen
		.getAllByRole('heading', { level: 2 })
		.find((heading) => heading.textContent.trim().replace(/^\*/, '') === 'Name')
		.closest('.col-12');

const textIn = (header) => getCellByHeader(nameSection(), header).querySelector('input').value;

describe('<AffectedGenomicModelCreatePage /> copying an AGM', { timeout: 30000 }, () => {
	beforeEach(() => {
		[getAGM, createAGM, navigate].forEach((stub) => stub.mockReset());
		getAGM.mockResolvedValue({ data: { entity: sourceAgm } });
		createAGM.mockResolvedValue({ data: { entity: { ...sourceAgm, id: 4343, curie: 'AGRKB:108000000000002' } } });
	});

	it("fills the full name from the AGM named in the URL, and creates the copy with it but not the source's ids", async () => {
		const user = userEvent.setup();
		renderPage();

		await waitFor(() => expect(textIn('Display Text')).toBe('wild isolate 1'), { timeout: 5000 });
		expect(getAGM).toHaveBeenCalledWith(SOURCE_CURIE);
		expect(textIn('Format Text')).toBe('wild isolate 1 format');
		expect(textIn('Synonym URL')).toBe('https://example.org');
		expect(getCellByHeader(nameSection(), 'Evidence').textContent).toContain('AGRKB:101');

		await user.click(screen.getByRole('button', { name: 'Save & Add Another' }));

		await waitFor(() => expect(createAGM).toHaveBeenCalledTimes(1));
		const { agmFullName } = createAGM.mock.calls[0][0];
		expect(agmFullName).toMatchObject({
			displayText: 'wild isolate 1',
			formatText: 'wild isolate 1 format',
			nameType: { id: 7, name: 'full_name' },
			synonymUrl: 'https://example.org',
			evidence: [{ id: 1, curie: 'AGRKB:101' }],
		});
		expect(agmFullName).not.toHaveProperty('id');
		expect(agmFullName).not.toHaveProperty('dateCreated');
	});
});
