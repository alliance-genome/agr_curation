import { fireEvent } from '@testing-library/react';
import { MutationTypesForm } from '../MutationTypesForm';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	renderAlleleForm,
	selectInColumn,
} from '../../__tests__/alleleFormTestUtils';
import { SearchService } from '../../../../service/SearchService';
import '../../../../tools/jest/setupTests';

vi.mock('../../../../service/SearchService');
vi.mock('../../../../service/useControlledVocabularyService');

const EVIDENCE = [
	{ id: 1, curie: 'AGRKB:101' },
	{ id: 2, curie: 'AGRKB:102' },
];
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };
const ERROR = { severity: 'error', message: 'second row is bad' };
const MUTATION_TYPES = [
	{ id: 11, curie: 'SO:0000001', name: 'point_mutation' },
	{ id: 12, curie: 'SO:0000002', name: 'deletion' },
];
const NEW_MUTATION_TYPE = { id: 13, curie: 'SO:0000003', name: 'insertion' };

const mutationType = () => ({ mutationTypes: MUTATION_TYPES, internal: false, evidence: EVIDENCE });

const renderForm = () => renderAlleleForm(MutationTypesForm, { alleleMutationTypes: [mutationType(), mutationType()] });

const savedRows = (getState) => getState().allele.alleleMutationTypes;

const EDITED_VALUE = true;
const EDITED_AFTER_ADD = [false, false, true];

describe('<MutationTypesForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn((endpoint) =>
			Promise.resolve({ results: [endpoint === 'soterm' ? NEW_MUTATION_TYPE : NEW_EVIDENCE] })
		);
	});

	it('saves a picked mutation type alongside the existing ones', async () => {
		const { container, getState } = renderForm();

		await pickSuggestionInColumn(container, 'Mutation Types', 'SO', /SO:0000003/, 1);

		expect(savedRows(getState).map((row) => row.mutationTypes)).toEqual([
			MUTATION_TYPES,
			[...MUTATION_TYPES, NEW_MUTATION_TYPE],
		]);
	});

	it('saves the remaining mutation types on the row a token was removed from', () => {
		const { container, getState } = renderForm();

		removeFirstTokenInColumn(container, 'Mutation Types', 1);

		expect(savedRows(getState).map((row) => row.mutationTypes)).toEqual([MUTATION_TYPES, [MUTATION_TYPES[1]]]);
	});

	it('saves a picked evidence alongside the existing ones', async () => {
		const { container, getState } = renderForm();

		await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/, 1);

		expect(savedRows(getState).map((row) => row.evidence)).toEqual([EVIDENCE, [...EVIDENCE, NEW_EVIDENCE]]);
	});

	it('saves the remaining evidence when a token is removed', () => {
		const { container, getState } = renderForm();

		removeFirstTokenInColumn(container, 'Evidence', 1);

		expect(savedRows(getState).map((row) => row.evidence)).toEqual([EVIDENCE, [EVIDENCE[1]]]);
	});

	it('saves internal as a boolean', () => {
		const { container, getState } = renderForm();

		selectInColumn(container, 'Internal', 'true', 1);

		expect(savedRows(getState).map((row) => row.internal)).toEqual([false, true]);
	});

	it('adds an empty mutation type at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getState } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Mutation Type' }));
		selectInColumn(container, 'Internal', 'true', 2);

		expect(savedRows(getState)[0]).toMatchObject({ internal: false });
		expect(savedRows(getState).map((row) => row.internal)).toEqual(EDITED_AFTER_ADD);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getState } = renderForm();
		const secondKey = savedRows(getState)[1].dataKey;

		clickDeleteInRow(container, 0);
		selectInColumn(container, 'Internal', 'true', 0);

		expect(savedRows(getState).map((row) => [row.dataKey, row.internal])).toEqual([[secondKey, EDITED_VALUE]]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getState, dispatch } = renderForm();
		const [firstKey, secondKey] = savedRows(getState).map((row) => row.dataKey);
		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'alleleMutationTypes',
			errorMessages: { [firstKey]: { mutationTypes: ERROR }, [secondKey]: { mutationTypes: ERROR } },
		});

		clickDeleteInRow(container, 0);

		expect(getState().entityStates.alleleMutationTypes.errorMessages).toEqual({
			[secondKey]: { mutationTypes: ERROR },
		});
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getState, dispatch } = renderForm();

		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'alleleMutationTypes',
			errorMessages: { [savedRows(getState)[1].dataKey]: { mutationTypes: ERROR } },
		});

		expect(getCellByHeader(container, 'Mutation Types', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Mutation Types', 0).textContent).not.toContain('second row is bad');
	});
});
