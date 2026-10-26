import React from 'react';
import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { alleleRejection, FORM_LOAD_WAIT, markAlleleExtinct, renderInRouter, storedAllele } from './alleleTestUtils';
import { SUCCESS_HOLD_MS } from '../../../hooks/useSaveProgress';

const { getAllele, saveAlleleDetail, getRows, replaceRows } = vi.hoisted(() => ({
	getAllele: vi.fn(),
	saveAlleleDetail: vi.fn(),
	getRows: vi.fn(),
	replaceRows: vi.fn(),
}));

const SYNONYMS = [
	{ id: 31, internal: false, displayText: 'syn-1', formatText: 'syn-1', nameType: { name: 'systematic_name' } },
	{ id: 32, internal: false, displayText: 'syn-2', formatText: 'syn-2', nameType: { name: 'systematic_name' } },
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

/**
 * A promise the test settles, so a request can be held in flight.
 *
 * @returns {{promise: Promise, resolve: Function, reject: Function}}
 */
const deferred = () => {
	let resolve;
	let reject;
	const promise = new Promise((resolvePromise, rejectPromise) => {
		resolve = resolvePromise;
		reject = rejectPromise;
	});
	return { promise, resolve, reject };
};

const savedSynonyms = { data: { entities: [SYNONYMS[1]] } };

const progress = () => screen.getByRole('status', { name: 'Save progress' });
const progressRow = (label) =>
	within(progress())
		.getByText(new RegExp(`^${label}:`))
		.closest('li');
const synonymsSection = () => screen.getByRole('heading', { name: 'Synonyms' }).closest('.grid').parentElement;

const deleteFirstSynonym = async (user) => {
	await waitFor(() => expect(getRows).toHaveBeenCalledWith('synonyms', storedAllele.id), FORM_LOAD_WAIT);
	await act(async () => {
		await new Promise((resolve) => setTimeout(resolve, 0));
	});
	const rows = await waitFor(() => {
		const tableRows = within(synonymsSection()).getAllByRole('row').slice(1);
		expect(tableRows).toHaveLength(2);
		return tableRows;
	}, FORM_LOAD_WAIT);
	await user.click(rows[0].querySelector('.pi-trash').closest('button'));
};

describe('<AlleleDetailPage /> save progress', { timeout: 60000 }, () => {
	beforeEach(() => {
		window.localStorage.removeItem('AlleleDetailFormSettings');
		getAllele.mockReset();
		saveAlleleDetail.mockReset();
		getRows.mockReset();
		replaceRows.mockReset();
		getAllele.mockResolvedValue({ data: { entity: storedAllele } });
		saveAlleleDetail.mockResolvedValue({ data: { entity: storedAllele } });
		getRows.mockImplementation((path) => Promise.resolve({ data: { entities: path === 'synonyms' ? SYNONYMS : [] } }));
	});

	it('Lists the allele and each edited list, pending until each is written, then closes', async () => {
		const user = userEvent.setup();
		const synonymsWrite = deferred();
		replaceRows.mockReturnValue(synonymsWrite.promise);
		await renderInRouter(<AlleleDetailPage />);

		await deleteFirstSynonym(user);
		await markAlleleExtinct(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Saving'));
		expect(progressRow('Allele')).toHaveTextContent('Allele: Saved');
		expect(within(progress()).getAllByRole('listitem')).toHaveLength(2);
		expect(within(progress()).getByRole('heading', { level: 1 })).toHaveTextContent('Saving...');

		await act(async () => synonymsWrite.resolve(savedSynonyms));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Saved'));
		expect(within(progress()).getByRole('heading', { level: 1 })).toHaveTextContent('Saved');
		await waitFor(() => expect(screen.queryByRole('status', { name: 'Save progress' })).not.toBeInTheDocument(), {
			timeout: SUCCESS_HOLD_MS + 2000,
		});
	});

	it('Shows a list that failed in red with its message, alongside the allele that saved', async () => {
		const user = userEvent.setup();
		replaceRows.mockRejectedValue({ response: { status: 400, data: { errorMessage: 'Could not update Synonyms' } } });
		await renderInRouter(<AlleleDetailPage />);

		await deleteFirstSynonym(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Failed'));
		expect(progressRow('Synonyms')).toHaveTextContent('Could not update Synonyms');
		expect(progressRow('Synonyms')).toHaveStyle({ color: 'var(--red-700)' });
		expect(progressRow('Allele')).toHaveTextContent('Allele: Saved');
		expect(within(progress()).getByRole('heading', { level: 1 })).toHaveTextContent('Save finished with errors');
	});

	it('Shows the allele failed while the lists are still written', async () => {
		const user = userEvent.setup();
		saveAlleleDetail.mockRejectedValue(alleleRejection);
		replaceRows.mockResolvedValue(savedSynonyms);
		await renderInRouter(<AlleleDetailPage />);

		await deleteFirstSynonym(user);
		await user.click(screen.getByRole('button', { name: 'Save' }));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Saved'));
		expect(progressRow('Allele')).toHaveTextContent('Allele: Failed');
		expect(progressRow('Allele')).toHaveTextContent('Could not update Allele');
	});

	it("Shows one row for a section's own save", async () => {
		const user = userEvent.setup();
		const synonymsWrite = deferred();
		replaceRows.mockReturnValue(synonymsWrite.promise);
		await renderInRouter(<AlleleDetailPage />);

		await deleteFirstSynonym(user);
		await user.click(screen.getByRole('button', { name: 'Save Synonyms' }));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Saving'));
		expect(within(progress()).getAllByRole('listitem')).toHaveLength(1);

		await act(async () => synonymsWrite.resolve(savedSynonyms));

		await waitFor(() => expect(progressRow('Synonyms')).toHaveTextContent('Synonyms: Saved'));
		expect(saveAlleleDetail).not.toHaveBeenCalled();
	});
});
