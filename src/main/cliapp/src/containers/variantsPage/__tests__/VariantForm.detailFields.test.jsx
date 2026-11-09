import { act, screen, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	fieldInput,
	fieldRow,
	pickSuggestionInField,
	selectInField,
	typeInField,
} from '../../../tools/jest/detailFieldTestUtils';
import { vocabularyTermsFor } from '../../../service/__mocks__/useControlledVocabularyService';

const PICKED_TERM = { id: 7, curie: 'SO:0000159', name: 'deletion' };

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [PICKED_TERM], totalResults: 1 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { VariantForm } = await import('../VariantForm');
const { useVariantReducer } = await import('../useVariantReducer');

const VARIANT = {
	id: 1,
	curie: 'AGRKB:104000000000001',
	taxon: { id: 3, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	variantType: { id: 4, curie: 'SO:1000002', name: 'substitution' },
	sourceGeneralConsequence: { id: 5, curie: 'SO:0001583', name: 'missense_variant' },
	variantStatus: vocabularyTermsFor('variant_status')[0],
	internal: false,
	obsolete: false,
};

const ONTOLOGY_FIELDS = [
	['Taxon', 'taxon', 'NCBITaxon:6239'],
	['Variant Type', 'variantType', 'SO:1000002'],
	['Source General Consequence', 'sourceGeneralConsequence', 'SO:0001583'],
];

/**
 * Renders the variant form against the real reducer, loaded with a variant on the detail page, and
 * returns a reader for the reducer state, which is what the page saves.
 */
const renderForm = ({ mode = 'detail' } = {}) => {
	const handle = {};
	const Harness = () => {
		const { variantState, variantDispatch } = useVariantReducer();
		handle.state = variantState;
		handle.dispatch = variantDispatch;
		return <VariantForm state={variantState} dispatch={variantDispatch} isVisible={() => true} mode={mode} />;
	};
	const result = renderWithClient(<Harness />);
	if (mode === 'detail') act(() => handle.dispatch({ type: 'SET', value: VARIANT }));
	return {
		...result,
		getVariant: () => handle.state.variant,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

describe('<VariantForm /> detail fields', () => {
	it.each(ONTOLOGY_FIELDS)('shows the loaded %s', (label, field, curie) => {
		renderForm();

		expect(fieldInput(label)).toHaveValue(curie);
	});

	it.each(ONTOLOGY_FIELDS)('saves a picked %s', async (label, field) => {
		const { getVariant } = renderForm();

		await pickSuggestionInField(label, 'del', /deletion/);

		expect(getVariant()[field]).toEqual(PICKED_TERM);
	});

	it.each(ONTOLOGY_FIELDS)('holds typed %s text keyed by curie', (label, field) => {
		const { getVariant } = renderForm();

		typeInField(label, 'SO:1');

		expect(getVariant()[field]).toEqual({ curie: 'SO:1' });
	});

	it.each(ONTOLOGY_FIELDS)('holds no %s once the input is emptied', (label, field) => {
		const { getVariant } = renderForm();

		typeInField(label, '');

		expect(getVariant()[field]).toBeUndefined();
	});

	it('saves the whole variant status term', () => {
		const { getVariant } = renderForm();
		const [, beta] = vocabularyTermsFor('variant_status');

		selectInField('Variant Status', beta.name);

		expect(getVariant().variantStatus).toEqual(beta);
	});

	it.each([
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('saves %s as a boolean', (label, field) => {
		const { getVariant } = renderForm();

		selectInField(label, 'true');
		expect(getVariant()[field]).toBe(true);

		selectInField(label, 'false');
		expect(getVariant()[field]).toBe(false);
	});

	it.each([
		...ONTOLOGY_FIELDS.map(([label, field]) => [label, field]),
		['Variant Status', 'variantStatus'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
		['Synonyms', 'synonyms'],
	])('shows an error reported for %s beside it', (label, field) => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { [field]: 'is bad' } });

		expect(within(fieldRow(label)).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});

	it.each(['Taxon', 'Variant Type'])('marks %s as required on the create page', (label) => {
		renderForm({ mode: 'create' });

		expect(fieldRow(label).querySelector('h2').textContent).toMatch(/^\*/);
	});
});
