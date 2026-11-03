import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';

const createAGM = vi.fn();
const navigate = vi.fn();

// msw cannot intercept this app's fetch based ApiClient, so stub the services directly.
vi.mock('../../../service/AffectedGenomicModelService', () => ({
	AffectedGenomicModelService: class {
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

const renderPage = () =>
	renderWithClient(
		<BrowserRouter>
			<AffectedGenomicModelCreatePage />
		</BrowserRouter>
	);

const button = (name) => screen.getByRole('button', { name });

// The AGM as the API returns it from create: its own ids and audit fields on it and on its rows.
const savedAgm = {
	id: 4242,
	curie: 'AGRKB:102000000000001',
	type: 'AffectedGenomicModel',
	taxon: { id: 9, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	subtype: { id: 3, name: 'strain' },
	internal: false,
	obsolete: false,
	dateCreated: '2026-10-23T00:00:00Z',
	createdBy: { uniqueId: 'curator1' },
	agmFullName: {
		id: 51,
		displayText: 'wild isolate 1',
		formatText: 'wild isolate 1',
		nameType: { name: 'full_name' },
		internal: false,
		dateCreated: '2026-10-23T00:00:00Z',
	},
	agmSynonyms: [],
	agmSecondaryIds: [{ id: 61, secondaryId: 'WB:WBStrain00000001', internal: false }],
};

describe('<AffectedGenomicModelCreatePage />', () => {
	beforeEach(() => {
		createAGM.mockReset();
		createAGM.mockResolvedValue({ data: { entity: savedAgm } });
		navigate.mockReset();
		window.localStorage.removeItem('AGMCreateFormSettings');
	});

	it('Keeps the saved values on save and add another', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.click(button('Save & Add Another'));

		await waitFor(() => expect(createAGM).toHaveBeenCalledTimes(1));
		expect(navigate).not.toHaveBeenCalled();
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:6239');
		});
		expect(container.querySelector('#displayText')).toHaveValue('wild isolate 1');
	});

	it('Creates another AGM from the kept values, without the saved ids', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Add Another'));
		await waitFor(() => expect(createAGM).toHaveBeenCalledTimes(1));
		await screen.findByText('AGM AGRKB:102000000000001 created');

		await user.click(button('Save & Add Another'));
		await waitFor(() => expect(createAGM).toHaveBeenCalledTimes(2));

		const payload = createAGM.mock.calls[1][0];
		expect(payload).not.toHaveProperty('id');
		expect(payload).not.toHaveProperty('curie');
		expect(payload).not.toHaveProperty('dateCreated');
		expect(payload.type).toEqual('AffectedGenomicModel');
		expect(payload.taxon.curie).toEqual('NCBITaxon:6239');
		expect(payload.subtype.name).toEqual('strain');
		expect(payload.agmFullName.displayText).toEqual('wild isolate 1');
		expect(payload.agmFullName).not.toHaveProperty('id');
		expect(payload.agmFullName).not.toHaveProperty('dateCreated');
		// A secondary ID names the saved AGM alone, so it is not copied.
		expect(payload.agmSecondaryIds).toEqual([]);
	});

	it('Clears the kept values', async () => {
		const user = userEvent.setup();
		const { container } = await renderPage();

		await user.click(button('Save & Add Another'));
		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('NCBITaxon:6239');
		});

		await user.click(button('Clear'));

		await waitFor(() => {
			expect(container.querySelector('input[name="taxon-input"]')).toHaveValue('');
		});
		expect(container.querySelector('#displayText')).not.toBeInTheDocument();
	});

	it('Opens the new AGM on save and close', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Close'));

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/agm/AGRKB:102000000000001'));
	});

	it('Names the new AGM in the success message', async () => {
		const user = userEvent.setup();
		await renderPage();

		await user.click(button('Save & Add Another'));

		expect(await screen.findByText('AGM AGRKB:102000000000001 created')).toBeInTheDocument();
	});
});
