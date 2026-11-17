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

const PICKED_TERM = { id: 7, curie: 'NCBITaxon:7955', name: 'Danio rerio' };

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [PICKED_TERM], totalResults: 1 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { AGMForm } = await import('../AGMForm');
const { useAGMReducer } = await import('../useAGMReducer');

const AGM = {
	id: 1,
	curie: 'AGRKB:103000000000001',
	subtype: vocabularyTermsFor('agm_subtype')[0],
	taxon: { id: 3, curie: 'NCBITaxon:6239', name: 'Caenorhabditis elegans' },
	internal: false,
	obsolete: false,
};

/**
 * Renders the AGM form against the real reducer, loaded with an AGM on the detail page, and returns a
 * reader for the reducer state, which is what the page saves.
 */
const renderForm = ({ mode = 'detail' } = {}) => {
	const handle = {};
	const Harness = () => {
		const { agmState, agmDispatch } = useAGMReducer();
		handle.state = agmState;
		handle.dispatch = agmDispatch;
		return <AGMForm state={agmState} dispatch={agmDispatch} isVisible={() => true} mode={mode} />;
	};
	const result = renderWithClient(<Harness />);
	if (mode === 'detail') act(() => handle.dispatch({ type: 'SET', value: AGM }));
	return {
		...result,
		getAGM: () => handle.state.agm,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

describe('<AGMForm /> detail fields', () => {
	it('shows the loaded taxon', () => {
		renderForm();

		expect(fieldInput('Taxon')).toHaveValue('NCBITaxon:6239');
	});

	it('saves a picked taxon', async () => {
		const { getAGM } = renderForm();

		await pickSuggestionInField('Taxon', 'Danio', /Danio rerio/);

		expect(getAGM().taxon).toEqual(PICKED_TERM);
	});

	it('holds typed taxon text keyed by curie', () => {
		const { getAGM } = renderForm();

		typeInField('Taxon', 'NCBITaxon:1');

		expect(getAGM().taxon).toEqual({ curie: 'NCBITaxon:1' });
	});

	it('holds no taxon once the input is emptied', () => {
		const { getAGM } = renderForm();

		typeInField('Taxon', '');

		expect(getAGM().taxon).toBeUndefined();
	});

	it('saves the whole sub type term', () => {
		const { getAGM } = renderForm();
		const [, beta] = vocabularyTermsFor('agm_subtype');

		selectInField('Sub Type', beta.name);

		expect(getAGM().subtype).toEqual(beta);
	});

	it.each([
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('saves %s as a boolean', (label, field) => {
		const { getAGM } = renderForm();

		selectInField(label, 'true');
		expect(getAGM()[field]).toBe(true);

		selectInField(label, 'false');
		expect(getAGM()[field]).toBe(false);
	});

	it.each([
		['Sub Type', 'subtype'],
		['Taxon', 'taxon'],
		['Internal', 'internal'],
		['Obsolete', 'obsolete'],
	])('shows an error reported for %s beside it', (label, field) => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { [field]: 'is bad' } });

		expect(within(fieldRow(label)).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});

	it.each(['Sub Type', 'Taxon'])('marks %s as required on the create page', (label) => {
		renderForm({ mode: 'create' });

		expect(fieldRow(label).querySelector('h2').textContent).toMatch(/^\*/);
	});
});
