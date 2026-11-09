import { useState } from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { SubResourcesProvider } from '../../../../components/SubResourcesContext';

const SEARCH_RESULT = {
	id: 9,
	curie: 'AGRKB:101',
	short_citation: 'Doe 2020',
	cross_references: [{ curie: 'PMID:101' }],
};

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [SEARCH_RESULT], totalResults: 1 }));
	},
}));

const { NewReferenceField } = await import('../NewReferenceField');

/**
 * Renders the picker against a stand-in for the references sub-resource that keeps its rows in state,
 * as the real one does, so tests can read back what the section would show.
 */
const renderField = () => {
	const handle = {};
	const Harness = () => {
		const [rows, setRows] = useState([]);
		handle.rows = rows;
		return (
			<SubResourcesProvider value={{ references: { rows, setRows } }}>
				<NewReferenceField />
			</SubResourcesProvider>
		);
	};
	const result = render(<Harness />);
	return { ...result, getRows: () => handle.rows };
};

const referenceInput = (container) => container.querySelector('input');
const addReference = () => fireEvent.click(screen.getByRole('button', { name: 'Add Reference' }));

describe('<NewReferenceField /> picking from the autocomplete', () => {
	it('adds a reference picked from the suggestions', async () => {
		const { container, getRows } = renderField();

		fireEvent.change(referenceInput(container), { target: { value: 'Doe' } });
		fireEvent.click(await screen.findByText(/AGRKB:101/, {}, { timeout: 3000 }));
		addReference();

		expect(getRows().map((row) => row.curie)).toEqual(['AGRKB:101']);
	});

	it('refuses typed text that was not picked', () => {
		const { container, getRows } = renderField();

		fireEvent.change(referenceInput(container), { target: { value: 'AGRKB:999' } });
		addReference();

		expect(screen.getByText('Must select reference from dropdown')).toBeInTheDocument();
		expect(getRows()).toEqual([]);
	});
});
