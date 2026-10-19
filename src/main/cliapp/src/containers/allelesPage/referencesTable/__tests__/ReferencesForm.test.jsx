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

const { ReferencesForm } = await import('../ReferencesForm');

const row = {
	dataKey: 'row-1',
	curie: 'AGRKB:101',
	shortCitation: 'Doe 2020',
	crossReferences: [{ referencedCurie: 'PMID:101' }],
	crossReferencesFilter: 'PMID:101',
};

const buildSubResource = (overrides = {}) => ({
	rows: [row],
	setRows: vi.fn(),
	errorMessages: {},
	isLoading: false,
	loadError: null,
	isSaving: false,
	isDirty: true,
	save: vi.fn(() => Promise.resolve({ isSuccess: true })),
	...overrides,
});

const renderForm = (overrides = {}, mode = 'detail') => {
	const references = buildSubResource(overrides);
	const result = renderWithClient(
		<SubResourcesProvider value={{ references }}>
			<ReferencesForm mode={mode} />
		</SubResourcesProvider>
	);
	return { ...result, references };
};

const saveButton = () => screen.getByRole('button', { name: /Save References/ });

describe('<ReferencesForm />', () => {
	it('Saves the references alone from its own button', async () => {
		const user = userEvent.setup();
		const { references } = renderForm();

		await user.click(saveButton());

		expect(references.save).toHaveBeenCalledTimes(1);
		expect(await screen.findByText('References Saved')).toBeInTheDocument();
	});

	it('Marks edited references as pending on the detail page', () => {
		renderForm();

		expect(screen.getByText('Pending Edits!')).toBeInTheDocument();
		expect(saveButton()).toBeEnabled();
	});

	it('Reports a refused save without claiming it saved', async () => {
		const user = userEvent.setup();
		renderForm({ save: vi.fn(() => Promise.resolve({ isSuccess: false, message: 'Could not update References' })) });

		await user.click(saveButton());

		expect(await screen.findByText('Could not update References')).toBeInTheDocument();
		expect(screen.queryByText('References Saved')).not.toBeInTheDocument();
	});

	it('Offers no save after the references failed to load, and says why', () => {
		renderForm({ loadError: new Error('unavailable') });

		expect(saveButton()).toBeDisabled();
		expect(
			screen.getByText('Could not load these references, so they cannot be saved. Reload the page to try again.')
		).toBeInTheDocument();
	});

	it('Offers no save while the references are still loading', () => {
		renderForm({ isLoading: true });

		expect(saveButton()).toBeDisabled();
	});

	it('Offers no save with nothing edited, and marks nothing', () => {
		renderForm({ isDirty: false });

		expect(saveButton()).toBeDisabled();
		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Shows no pending edits on the create page, where every row is unsaved', () => {
		renderForm({}, 'create');

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
	});

	it('Offers no save on the create page, which has no allele to save against', () => {
		renderForm({}, 'create');

		expect(screen.queryByRole('button', { name: /Save References/ })).not.toBeInTheDocument();
		expect(screen.getByRole('button', { name: 'Add Reference' })).toBeInTheDocument();
	});
});
