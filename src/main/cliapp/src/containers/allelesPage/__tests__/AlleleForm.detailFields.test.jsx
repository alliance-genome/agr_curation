import { act, screen, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	fieldInput,
	fieldRow,
	pickSuggestionInField,
	selectInField,
	typeInField,
} from '../../../tools/jest/detailFieldTestUtils';

const PICKED_TERM = { id: 7, curie: 'NCBITaxon:7955', name: 'Danio rerio' };

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [PICKED_TERM], totalResults: 1 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { AlleleForm } = await import('../AlleleForm');
const { useAlleleReducer } = await import('../useAlleleReducer');
const { useAlleleSubResources } = await import('../useAlleleSubResources');
const { SubResourcesProvider } = await import('../../../components/SubResourcesContext');

const ALLELE = {
	id: 1,
	curie: 'AGRKB:102000000000001',
	taxon: { id: 3, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	inCollection: { id: 4, name: 'collection_one' },
	isExtinct: false,
	internal: false,
	obsolete: false,
};

/**
 * Renders the allele form against the real reducer, loaded with `allele`, and returns a reader for
 * the reducer state, which is what the page saves.
 */
const renderForm = ({ mode = 'detail', pendingFields } = {}) => {
	const handle = {};
	const Harness = () => {
		const { alleleState, alleleDispatch } = useAlleleReducer(mode);
		const { subResources } = useAlleleSubResources();
		handle.state = alleleState;
		handle.dispatch = alleleDispatch;
		return (
			<SubResourcesProvider value={subResources}>
				<AlleleForm
					state={alleleState}
					dispatch={alleleDispatch}
					isVisible={() => true}
					mode={mode}
					pendingFields={pendingFields}
				/>
			</SubResourcesProvider>
		);
	};
	const result = renderWithClient(<Harness />);
	if (mode === 'detail') act(() => handle.dispatch({ type: 'SET', value: ALLELE }));
	return {
		...result,
		getAllele: () => handle.state.allele,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

describe('<AlleleForm /> detail fields', () => {
	it('shows the loaded taxon and collection', () => {
		renderForm();

		expect(fieldInput('Taxon')).toHaveValue('NCBITaxon:6239');
		expect(fieldInput('In Collection')).toHaveValue('collection_one');
	});

	it('saves a picked taxon', async () => {
		const { getAllele } = renderForm();

		await pickSuggestionInField('Taxon', 'Danio', /Danio rerio/);

		expect(getAllele().taxon).toEqual(PICKED_TERM);
	});

	it('holds typed taxon text keyed by curie', () => {
		const { getAllele } = renderForm();

		typeInField('Taxon', 'NCBITaxon:1');

		expect(getAllele().taxon).toEqual({ curie: 'NCBITaxon:1' });
	});

	it('holds a blank taxon once the input is emptied', () => {
		const { getAllele } = renderForm();

		typeInField('Taxon', '');

		expect(getAllele().taxon).toEqual({ curie: '' });
	});

	it('saves a picked collection', async () => {
		const { getAllele } = renderForm();

		await pickSuggestionInField('In Collection', 'Danio', /Danio rerio/);

		expect(getAllele().inCollection).toEqual(PICKED_TERM);
	});

	it('holds typed collection text keyed by name', () => {
		const { getAllele } = renderForm();

		typeInField('In Collection', 'collection_two');

		expect(getAllele().inCollection).toEqual({ name: 'collection_two' });
	});

	it('holds no collection once the input is emptied', () => {
		const { getAllele } = renderForm();

		typeInField('In Collection', '');

		expect(getAllele().inCollection).toBeUndefined();
	});

	it.each([
		['Is Extinct', 'isExtinct'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('saves %s as a boolean', (label, field) => {
		const { getAllele } = renderForm();

		selectInField(label, 'true');
		expect(getAllele()[field]).toBe(true);

		selectInField(label, 'false');
		expect(getAllele()[field]).toBe(false);
	});

	it.each([
		['Taxon', 'taxon'],
		['In Collection', 'inCollection'],
		['Is Extinct', 'isExtinct'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('shows an error reported for %s beside it', (label, field) => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { [field]: 'is bad' } });

		expect(within(fieldRow(label)).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});

	it.each([
		['Taxon', 'taxon'],
		['In Collection', 'inCollection'],
		['Is Extinct', 'isExtinct'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('marks %s as pending', (label, field) => {
		renderForm({ pendingFields: new Set([field]) });

		expect(within(fieldRow(label)).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('marks the taxon as required on the create page', () => {
		renderForm({ mode: 'create' });

		expect(fieldRow('Taxon').querySelector('h2').textContent).toMatch(/^\*/);
	});
});
