import React from 'react';
import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../../tools/jest/utils';

// The Internal and Obsolete editors resolve their boolean terms through a SearchService, and the
// descriptor autocomplete builds one of its own. Left unstubbed they reach ApiClient after the test
// has finished and throw in a timer.
vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const { CrossReferencesTable } = await import('../CrossReferencesTable');

const PMID = {
	id: 9,
	prefix: 'PMID',
	resourcePages: [
		{ id: 1, name: 'default' },
		{ id: 2, name: 'gene' },
	],
};

const ZFIN = {
	id: 12,
	prefix: 'ZFIN',
	resourcePages: [{ id: 30, name: 'zfin-homepage' }],
};

const buildRow = (overrides = {}) => ({
	dataKey: 'row-1',
	displayName: 'PMID:1',
	referencedCurie: 'PMID:1',
	resourceDescriptor: PMID,
	resourceDescriptorPage: { id: 1, name: 'default' },
	internal: false,
	obsolete: false,
	...overrides,
});

const renderTable = (props = {}) => {
	const onFieldChange = vi.fn();
	const deletionHandler = vi.fn();
	const rows = props.crossReferences ?? [buildRow()];

	const ui = (crossReferences) => (
		<CrossReferencesTable
			crossReferences={crossReferences}
			editingRows={Object.fromEntries(crossReferences.map((row) => [row.dataKey, true]))}
			onRowEditChange={() => null}
			errorMessages={{}}
			deletionHandler={deletionHandler}
			onFieldChange={onFieldChange}
			showObsolete={props.showObsolete ?? false}
		/>
	);

	const { rerender, ...rest } = renderWithClient(ui(rows));

	return { ...rest, onFieldChange, deletionHandler, rerenderWith: (nextRows) => rerender(ui(nextRows)) };
};

// PrimeReact puts the aria-label on a hidden input that mirrors the selection, and opens the panel
// from the dropdown root.
const pageInput = () => screen.getByLabelText('resourceDescriptorPage');
const pageRoot = () => pageInput().closest('.p-dropdown');
const openPageDropdown = (user) => user.click(pageRoot());

describe('CrossReferencesTable', () => {
	it('Shows the stored page as the selection, matched to the descriptor by id', () => {
		renderTable();

		expect(pageInput()).toHaveValue('default');
	});

	it('Offers the pages of the row descriptor', async () => {
		const user = userEvent.setup();
		renderTable();

		await openPageDropdown(user);

		expect(await screen.findByText('gene')).toBeInTheDocument();
	});

	// The page dropdown is the one editor whose options depend on another field in the same row.
	// PrimeReact freezes editorOptions.rowData when the row opens, so reading props.rowData here would
	// keep offering the old descriptor's pages after a new one is chosen.
	it('Offers the new descriptor pages after the owning state changes, not the snapshot ones', async () => {
		const user = userEvent.setup();
		const { rerenderWith } = renderTable();

		rerenderWith([buildRow({ resourceDescriptor: ZFIN, resourceDescriptorPage: null })]);
		await openPageDropdown(user);

		expect(await screen.findByText('zfin-homepage')).toBeInTheDocument();
		expect(screen.queryByText('gene')).not.toBeInTheDocument();
	});

	it('Tells the curator to choose a descriptor before a page', () => {
		renderTable({ crossReferences: [buildRow({ resourceDescriptor: null, resourceDescriptorPage: null })] });

		expect(pageRoot().querySelector('.p-dropdown-label')).toHaveTextContent('Select a resource descriptor first');
	});

	it('Reports a chosen page against the row dataKey', async () => {
		const user = userEvent.setup();
		const { onFieldChange } = renderTable();

		await openPageDropdown(user);
		await user.click(await screen.findByText('gene'));

		expect(onFieldChange).toHaveBeenCalledWith('row-1', 'resourceDescriptorPage', { id: 2, name: 'gene' });
	});

	it('Reports a display name edit against the row dataKey', async () => {
		const user = userEvent.setup();
		const { container, onFieldChange } = renderTable();

		await user.type(container.querySelector('#displayName'), 'X');

		expect(onFieldChange).toHaveBeenCalledWith('row-1', 'displayName', expect.stringContaining('X'));
	});

	it('Reports a referenced curie edit against the row dataKey', async () => {
		const user = userEvent.setup();
		const { container, onFieldChange } = renderTable();

		await user.type(container.querySelector('#referencedCurie'), 'Y');

		expect(onFieldChange).toHaveBeenCalledWith('row-1', 'referencedCurie', expect.stringContaining('Y'));
	});

	it('Deletes by dataKey rather than row index', async () => {
		const user = userEvent.setup();
		const { deletionHandler } = renderTable();

		await user.click(screen.getByRole('button', { name: '' }));

		expect(deletionHandler).toHaveBeenCalledWith(expect.anything(), 'row-1');
	});

	it('Offers Obsolete only when asked to', () => {
		const { unmount } = renderTable();
		expect(screen.queryByText('Obsolete')).not.toBeInTheDocument();
		unmount();

		renderTable({ showObsolete: true });
		expect(screen.getByText('Obsolete')).toBeInTheDocument();
	});
});
