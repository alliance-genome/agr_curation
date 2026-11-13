import { act, screen, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	fieldInput,
	fieldRow,
	pickSuggestionInField,
	selectInField,
	typeInField,
} from '../../../tools/jest/detailFieldTestUtils';

const PICKED_TERM = { id: 7, curie: 'SO:0001217', name: 'protein_coding_gene' };

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [PICKED_TERM], totalResults: 1 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { GeneForm } = await import('../GeneForm');
const { useGeneReducer } = await import('../useGeneReducer');
const { useGeneSubResources } = await import('../useGeneSubResources');
const { SubResourcesProvider } = await import('../../../components/SubResourcesContext');

const GENE = {
	id: 1,
	curie: 'AGRKB:101000000000001',
	geneType: { id: 4, curie: 'SO:0001263', name: 'ncRNA_gene' },
	taxon: { id: 3, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	internal: false,
	obsolete: false,
};

const ONTOLOGY_FIELDS = [
	['Gene Type', 'geneType', 'SO:0001263'],
	['Taxon', 'taxon', 'NCBITaxon:6239'],
];

/**
 * Renders the gene form against the real reducer, loaded with a gene, and returns a reader for the
 * reducer state, which is what the page saves. With no gene id the list sections are not read.
 */
const renderForm = ({ pendingFields } = {}) => {
	const handle = {};
	const Harness = () => {
		const { geneState, geneDispatch } = useGeneReducer();
		const { subResources } = useGeneSubResources();
		handle.state = geneState;
		handle.dispatch = geneDispatch;
		return (
			<SubResourcesProvider value={subResources}>
				<GeneForm state={geneState} dispatch={geneDispatch} isVisible={() => true} pendingFields={pendingFields} />
			</SubResourcesProvider>
		);
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: GENE }));
	return {
		...result,
		getGene: () => handle.state.gene,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

describe('<GeneForm /> detail fields', () => {
	it.each(ONTOLOGY_FIELDS)('shows the loaded %s', (label, field, curie) => {
		renderForm();

		expect(fieldInput(label)).toHaveValue(curie);
	});

	it.each(ONTOLOGY_FIELDS)('saves a picked %s', async (label, field) => {
		const { getGene } = renderForm();

		await pickSuggestionInField(label, 'protein', /protein_coding_gene/);

		expect(getGene()[field]).toEqual(PICKED_TERM);
	});

	it.each(ONTOLOGY_FIELDS)('holds typed %s text keyed by curie', (label, field) => {
		const { getGene } = renderForm();

		typeInField(label, 'SO:1');

		expect(getGene()[field]).toEqual({ curie: 'SO:1' });
	});

	it.each(ONTOLOGY_FIELDS)('holds a blank %s once the input is emptied', (label, field) => {
		const { getGene } = renderForm();

		typeInField(label, '');

		expect(getGene()[field]).toEqual({ curie: '' });
	});

	it.each([
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('saves %s as a boolean', (label, field) => {
		const { getGene } = renderForm();

		selectInField(label, 'true');
		expect(getGene()[field]).toBe(true);

		selectInField(label, 'false');
		expect(getGene()[field]).toBe(false);
	});

	it.each([
		['Gene Type', 'geneType'],
		['Taxon', 'taxon'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
		['GCRP Cross Reference', 'gcrpCrossReference'],
	])('shows an error reported for %s beside it', (label, field) => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { [field]: 'is bad' } });

		expect(within(fieldRow(label)).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});

	it.each([
		['Gene Type', 'geneType'],
		['Taxon', 'taxon'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('marks %s as pending', (label, field) => {
		renderForm({ pendingFields: new Set([field]) });

		expect(within(fieldRow(label)).getByText('Pending Edits!')).toBeInTheDocument();
	});

	it.each(['Gene Type', 'Taxon'])('marks %s as required', (label) => {
		renderForm();

		expect(fieldRow(label).querySelector('h2').textContent).toMatch(/^\*/);
	});

	it('shows an error given as an object for GCRP Cross Reference beside it', () => {
		const { dispatch } = renderForm();

		dispatch({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { gcrpCrossReference: { severity: 'error', message: 'is bad' } },
		});

		expect(within(fieldRow('GCRP Cross Reference')).getByText('is bad')).toBeInTheDocument();
	});
});
