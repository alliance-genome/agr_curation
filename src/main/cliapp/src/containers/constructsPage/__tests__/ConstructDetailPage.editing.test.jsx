import React from 'react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { act, fireEvent, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../tools/jest/utils';
import { fieldRow, selectInField } from '../../../tools/jest/detailFieldTestUtils';

const { getConstruct, saveConstructDetail, getRows, replaceRows } = vi.hoisted(() => ({
	getConstruct: vi.fn(),
	saveConstructDetail: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
}));

vi.mock('../../../service/ConstructService', async () =>
	(await import('./constructServiceMocks')).mockConstructService({ getConstruct, saveConstructDetail })
);
vi.mock('../../../service/ConstructSubResourceService', async () =>
	(await import('./constructServiceMocks')).mockConstructSubResourceService({ getRows, replaceRows })
);
vi.mock('../../../service/SearchService', async () =>
	(await import('../../allelesPage/__tests__/alleleServiceMocks')).mockSearchService()
);

const ConstructDetailPage = (await import('../ConstructDetailPage')).default;

// Each wait follows a stubbed call through a full render of the page, which can outlast the default under load.
const PAGE_LOAD_WAIT = { timeout: 5000 };

const IDENTIFIER = 'ZFIN:ZDB-TGCONSTRCT-070117-175';

const storedConstruct = {
	id: 7,
	curie: 'AGRKB:102000000000001',
	primaryExternalId: IDENTIFIER,
	constructSymbol: { id: 11, displayText: 'Tg(hsp70l:GFP)', formatText: 'Tg(hsp70l:GFP)', internal: false },
	placeholder: false,
	internal: false,
	obsolete: false,
};

const STORED_LISTS = { secondaryids: ['ZFIN:ZDB-TGCONSTRCT-1', 'ZFIN:ZDB-TGCONSTRCT-2'] };

const saveButton = () => screen.getByRole('button', { name: 'Save' });
const section = (name) => screen.getByRole('heading', { name }).closest('.grid').parentElement;
const secondaryIdRows = () => within(section('Secondary IDs')).getAllByRole('row').slice(1);

const renderLoaded = async () => {
	renderWithClient(
		<MemoryRouter initialEntries={[`/construct/${IDENTIFIER}`]}>
			<Routes>
				<Route path="/construct/:identifier" element={<ConstructDetailPage />} />
			</Routes>
		</MemoryRouter>
	);
	await waitFor(() => expect(secondaryIdRows()).toHaveLength(2), PAGE_LOAD_WAIT);
	// Lets the rows' editors settle before they are clicked.
	await act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});
};

const deleteFirstSecondaryId = async (user) =>
	user.click(secondaryIdRows()[0].querySelector('.pi-trash').closest('button'));

describe('<ConstructDetailPage /> editing', { timeout: 60000 }, () => {
	beforeEach(() => {
		[getConstruct, saveConstructDetail, getRows, replaceRows].forEach((stub) => stub.mockReset());
		getConstruct.mockResolvedValue({ data: { entity: storedConstruct } });
		saveConstructDetail.mockImplementation((construct) => Promise.resolve({ data: { entity: construct } }));
		getRows.mockImplementation((path) => Promise.resolve({ data: { entities: STORED_LISTS[path] ?? [] } }));
		replaceRows.mockImplementation((path, constructId, rows) => Promise.resolve({ data: { entities: rows } }));
	});

	it('reads the construct by its identifier and each of its lists by its id, with nothing pending', async () => {
		await renderLoaded();

		expect(getConstruct).toHaveBeenCalledWith(IDENTIFIER);
		['synonyms', 'secondaryids', 'cassetteassociations', 'relatednotes', 'references'].forEach((path) =>
			expect(getRows).toHaveBeenCalledWith(path, storedConstruct.id)
		);
		expect(saveButton()).toBeDisabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
		['Save Synonyms', 'Save Secondary IDs', 'Save Cassettes', 'Save Related Notes', 'Save References'].forEach((name) =>
			expect(screen.getByRole('button', { name })).toBeDisabled()
		);
	});

	it('marks an edited field as pending until it is set back', async () => {
		await renderLoaded();

		selectInField('Placeholder', 'true');

		expect(within(fieldRow('Placeholder')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(saveButton()).toBeEnabled();

		selectInField('Placeholder', 'false');

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(saveButton()).toBeDisabled();
	});

	it("saves one list alone with its section's own Save, leaving the other edits pending", async () => {
		const user = userEvent.setup();
		await renderLoaded();

		selectInField('Obsolete', 'true');
		await deleteFirstSecondaryId(user);
		expect(within(section('Secondary IDs')).getByText('Pending Edits!')).toBeInTheDocument();
		await user.click(screen.getByRole('button', { name: 'Save Secondary IDs' }));

		await waitFor(() =>
			expect(replaceRows).toHaveBeenCalledWith('secondaryids', storedConstruct.id, ['ZFIN:ZDB-TGCONSTRCT-2'])
		);
		expect(saveConstructDetail).not.toHaveBeenCalled();
		await waitFor(() => expect(within(section('Secondary IDs')).queryByText('Pending Edits!')).not.toBeInTheDocument());
		expect(within(fieldRow('Obsolete')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(saveButton()).toBeEnabled();
	});

	it('writes the construct through the detail endpoint and then each edited list, showing the progress of each', async () => {
		const user = userEvent.setup();
		await renderLoaded();

		selectInField('Internal', 'true');
		await deleteFirstSecondaryId(user);
		fireEvent.click(saveButton());
		const progress = screen.getByRole('status', { name: 'Save progress' });

		expect(
			within(progress)
				.getAllByRole('listitem')
				.map((item) => item.textContent.split(':')[0])
		).toEqual(['Construct', 'Secondary IDs']);
		await waitFor(() => expect(replaceRows).toHaveBeenCalledTimes(1));
		expect(saveConstructDetail).toHaveBeenCalledTimes(1);
		const [payload] = saveConstructDetail.mock.calls[0];
		expect(payload).toEqual(expect.objectContaining({ id: storedConstruct.id, internal: true }));
		expect(await screen.findByText('Construct Saved')).toBeInTheDocument();
		await waitFor(() => expect(saveButton()).toBeDisabled());
	});

	it('keeps a list the API rejects pending, with its error on its row, while the construct saves', async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValue({
			response: {
				status: 400,
				data: {
					errorMessage: 'Could not update Secondary IDs for: [7]',
					supplementalData: { errorMap: { secondaryIdentifiers: { 0: { secondaryId: 'Non-unique value' } } } },
				},
			},
		});
		await renderLoaded();

		selectInField('Internal', 'true');
		await deleteFirstSecondaryId(user);
		await user.click(saveButton());

		await waitFor(() => expect(saveConstructDetail).toHaveBeenCalledTimes(1));
		expect(await screen.findByText(/Could not update Secondary IDs/)).toBeInTheDocument();
		expect(within(section('Secondary IDs')).getByText('Non-unique value')).toBeInTheDocument();
		expect(within(section('Secondary IDs')).getByText('Pending Edits!')).toBeInTheDocument();
		expect(within(fieldRow('Internal')).queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(saveButton()).toBeEnabled();
	});

	it('says no construct has an identifier the API does not know', async () => {
		getConstruct.mockResolvedValue({ data: {} });
		renderWithClient(
			<MemoryRouter initialEntries={[`/construct/${IDENTIFIER}`]}>
				<Routes>
					<Route path="/construct/:identifier" element={<ConstructDetailPage />} />
				</Routes>
			</MemoryRouter>
		);

		await waitFor(
			() => expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Construct not found'),
			PAGE_LOAD_WAIT
		);
		expect(screen.getByRole('link', { name: 'Back to the construct table' })).toHaveAttribute('href', '/constructs');
		expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
	});
});
