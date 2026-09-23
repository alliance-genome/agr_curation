import React from 'react';
import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../../tools/jest/utils';
import { SubResourcesProvider } from '../../../../components/SubResourcesContext';

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const { CrossReferencesForm } = await import('../CrossReferencesForm');

const row = {
	dataKey: 'row-1',
	id: 500,
	displayName: 'PMID:1',
	referencedCurie: 'PMID:1',
	resourceDescriptor: { id: 9, prefix: 'PMID', resourcePages: [{ id: 1, name: 'default' }] },
	resourceDescriptorPage: { id: 1, name: 'default' },
	internal: false,
	obsolete: false,
};

const buildSubResource = (overrides = {}) => ({
	crossReferences: [row],
	setCrossReferences: vi.fn(),
	errorMessages: {},
	loadError: null,
	...overrides,
});

const renderForm = (overrides = {}, mode = 'detail') => {
	const crossReferences = buildSubResource(overrides);
	const result = renderWithClient(
		<SubResourcesProvider value={{ crossReferences }}>
			<CrossReferencesForm mode={mode} />
		</SubResourcesProvider>
	);
	return { ...result, crossReferences };
};

describe('CrossReferencesForm', () => {
	// The page's own Save writes these rows along with the allele.
	it('Offers no save of its own, on either page', () => {
		const { unmount } = renderForm();
		expect(screen.queryByRole('button', { name: /Save/ })).not.toBeInTheDocument();
		unmount();

		renderForm({}, 'create');
		expect(screen.queryByRole('button', { name: /Save/ })).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Add Cross Reference/ })).toBeInTheDocument();
	});

	it('Says so when the rows failed to load, since the page then cannot save them', () => {
		renderForm({ crossReferences: [], loadError: new Error('network') });

		expect(screen.getByText(/Could not load these cross references/)).toBeInTheDocument();
	});

	// Every other section of the form hides its table until it has a row, so an allele with no cross
	// references shows the heading and the button alone.
	it('Shows no table until there is a row', () => {
		const { unmount } = renderForm({ crossReferences: [] });
		expect(screen.queryByRole('columnheader', { name: 'Display Name' })).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Add Cross Reference/ })).toBeInTheDocument();
		unmount();

		renderForm();
		expect(screen.getByRole('columnheader', { name: 'Display Name' })).toBeInTheDocument();
	});

	it('Adds a blank row', async () => {
		const user = userEvent.setup();
		const { crossReferences } = renderForm();

		await user.click(screen.getByRole('button', { name: /Add Cross Reference/ }));

		expect(crossReferences.setCrossReferences).toHaveBeenCalled();
		const updater = crossReferences.setCrossReferences.mock.calls[0][0];
		expect(updater([row])).toHaveLength(2);
	});

	it('Removes the row it is asked to delete, by dataKey', async () => {
		const user = userEvent.setup();
		const { crossReferences } = renderForm();

		await user.click(screen.getByRole('button', { name: '' }));

		const updater = crossReferences.setCrossReferences.mock.calls[0][0];
		expect(updater([row, { ...row, dataKey: 'row-2' }])).toEqual([{ ...row, dataKey: 'row-2' }]);
	});
});
