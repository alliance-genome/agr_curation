import { act, fireEvent, screen } from '@testing-library/react';
import { renderWithClient } from '../../../../tools/jest/utils';

const SEARCH_RESULT = {
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
const { useVariantReducer } = await import('../../useVariantReducer');

/**
 * Renders the picker against the real variant reducer, loaded with a variant holding no references,
 * and returns a reader for the references the page would save.
 */
const renderField = () => {
	const handle = {};
	const Harness = () => {
		const { variantState, variantDispatch } = useVariantReducer();
		handle.state = variantState;
		handle.dispatch = variantDispatch;
		return <NewReferenceField state={variantState} dispatch={variantDispatch} />;
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: { id: 1, references: [] } }));
	return { ...result, getReferences: () => handle.state.variant.references };
};

const referenceInput = (container) => container.querySelector('input');
const addReference = () => fireEvent.click(screen.getByRole('button', { name: 'Add Reference' }));

describe('<NewReferenceField /> on the variant pages', () => {
	it('adds a reference picked from the suggestions', async () => {
		const { container, getReferences } = renderField();

		fireEvent.change(referenceInput(container), { target: { value: 'Doe' } });
		fireEvent.click(await screen.findByText(/AGRKB:101/, {}, { timeout: 3000 }));
		addReference();

		expect(getReferences().map((reference) => reference.curie)).toEqual(['AGRKB:101']);
		expect(getReferences()[0]).toMatchObject({ shortCitation: 'Doe 2020', crossReferencesFilter: 'PMID:101' });
	});

	it('refuses typed text that was not picked', () => {
		const { container, getReferences } = renderField();

		fireEvent.change(referenceInput(container), { target: { value: 'AGRKB:999' } });
		addReference();

		expect(screen.getByText('Must select reference from dropdown')).toBeInTheDocument();
		expect(getReferences()).toEqual([]);
	});
});
