import React from 'react';
import { screen, waitFor } from '@testing-library/react';
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
	isLoading: false,
	loadError: null,
	isSaving: false,
	save: vi.fn(() => Promise.resolve({ isSuccess: true })),
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

const saveButton = () => screen.getByRole('button', { name: /Save Cross References/ });

describe('CrossReferencesForm', () => {
	it('Saves the rows and says so', async () => {
		const user = userEvent.setup();
		const { crossReferences } = renderForm();

		await user.click(saveButton());

		await waitFor(() => expect(crossReferences.save).toHaveBeenCalled());
		expect(await screen.findByText('Cross References Saved')).toBeInTheDocument();
	});

	it('Reports a failed save without claiming success', async () => {
		const user = userEvent.setup();
		const { crossReferences } = renderForm({
			save: vi.fn(() => Promise.resolve({ isSuccess: false, message: 'Could not update CrossReferences' })),
		});

		await user.click(saveButton());

		await waitFor(() => expect(crossReferences.save).toHaveBeenCalled());
		expect(await screen.findByText('Could not update CrossReferences')).toBeInTheDocument();
		expect(screen.queryByText('Cross References Saved')).not.toBeInTheDocument();
	});

	// A save replaces the stored list with what is on screen, so saving a table that has not been read
	// would submit an empty list and delete every cross reference the allele has.
	it('Will not save while the rows are still loading', () => {
		renderForm({ crossReferences: [], isLoading: true });

		expect(saveButton()).toBeDisabled();
	});

	it('Will not save when the rows failed to load, and says why', () => {
		renderForm({ crossReferences: [], loadError: new Error('network') });

		expect(saveButton()).toBeDisabled();
		expect(screen.getByText(/Could not load these cross references/)).toBeInTheDocument();
	});

	it('Offers no save on the create page, which has no allele to save against', () => {
		renderForm({}, 'create');

		expect(screen.queryByRole('button', { name: /Save Cross References/ })).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: /Add Cross Reference/ })).toBeInTheDocument();
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
