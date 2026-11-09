import { act, screen, within } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { fieldRow, selectInField } from '../../../tools/jest/detailFieldTestUtils';

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { CassetteForm } = await import('../CassetteForm');
const { useCassetteReducer } = await import('../detail/useCassetteReducer');

const BOOLEAN_FIELDS = [
	['Placeholder', 'placeholder'],
	['Internal', 'internal'],
	['Obsolete', 'obsolete'],
];
const BOOLEAN_LABELS = BOOLEAN_FIELDS.map(([label]) => label);

/**
 * Renders the cassette form's boolean fields against the real reducer, loaded with a cassette, and
 * returns a reader for the reducer state, which is what the page saves.
 */
const renderForm = () => {
	const handle = {};
	const Harness = () => {
		const { cassetteState, cassetteDispatch } = useCassetteReducer();
		handle.state = cassetteState;
		handle.dispatch = cassetteDispatch;
		return (
			<CassetteForm
				state={cassetteState}
				dispatch={cassetteDispatch}
				componentSections={[]}
				isVisible={(field) => BOOLEAN_LABELS.includes(field)}
			/>
		);
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: { id: 1, placeholder: false, internal: false, obsolete: false } }));
	return {
		...result,
		getCassette: () => handle.state.cassette,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

describe('<CassetteForm /> detail fields', () => {
	it.each(BOOLEAN_FIELDS)('saves %s as a boolean', (label, field) => {
		const { getCassette } = renderForm();

		selectInField(label, 'true');
		expect(getCassette()[field]).toBe(true);

		selectInField(label, 'false');
		expect(getCassette()[field]).toBe(false);
	});

	it.each(BOOLEAN_FIELDS)('shows an error reported for %s beside it', (label, field) => {
		const { dispatch } = renderForm();

		dispatch({ type: 'UPDATE_ERROR_MESSAGES', errorMessages: { [field]: 'is bad' } });

		expect(within(fieldRow(label)).getByText('is bad')).toBeInTheDocument();
		expect(screen.getAllByText('is bad')).toHaveLength(1);
	});
});
