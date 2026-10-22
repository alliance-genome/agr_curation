import { fireEvent } from '@testing-library/react';
import { InheritanceModesForm } from '../InheritanceModesForm';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	renderAlleleForm,
	selectInColumn,
	typeInColumn,
} from '../../__tests__/alleleFormTestUtils';
import { SearchService } from '../../../../service/SearchService';
import { vocabularyTermsFor } from '../../../../service/__mocks__/useControlledVocabularyService';
import '../../../../tools/jest/setupTests';

vi.mock('../../../../service/SearchService');
vi.mock('../../../../service/useControlledVocabularyService');

const EVIDENCE = [
	{ id: 1, curie: 'AGRKB:101' },
	{ id: 2, curie: 'AGRKB:102' },
];
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };
const ERROR = { severity: 'error', message: 'second row is bad' };
const PHENOTYPE_TERM = { id: 31, curie: 'HP:0000001', name: 'All' };

const inheritanceMode = (statement) => ({
	inheritanceMode: null,
	phenotypeTerm: null,
	phenotypeStatement: statement,
	internal: false,
	evidence: EVIDENCE,
});

const renderForm = () =>
	renderAlleleForm(InheritanceModesForm, {
		alleleInheritanceModes: [inheritanceMode('first'), inheritanceMode('second')],
	});

const savedRows = (getState) => getState().allele.alleleInheritanceModes;

const EDITED_VALUE = 'edited';
const EDITED_AFTER_ADD = ['', 'first', 'edited'];

describe('<InheritanceModesForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn((endpoint) =>
			Promise.resolve({ results: [endpoint === 'phenotypeterm' ? PHENOTYPE_TERM : NEW_EVIDENCE] })
		);
	});

	it('saves the whole inheritance mode term on the row it was picked in', () => {
		const { container, getState } = renderForm();

		selectInColumn(container, 'Inheritance Mode', 'allele_inheritance_mode_beta', 1);

		expect(savedRows(getState).map((row) => row.inheritanceMode)).toEqual([
			null,
			vocabularyTermsFor('allele_inheritance_mode')[1],
		]);
	});

	it('saves a picked phenotype term on the row it was picked in', async () => {
		const { container, getState } = renderForm();

		await pickSuggestionInColumn(container, 'Phenotype Term', 'HP', /HP:0000001/, 1);

		expect(savedRows(getState).map((row) => row.phenotypeTerm)).toEqual([null, PHENOTYPE_TERM]);
	});

	it('saves a typed phenotype statement on the row it was typed in', () => {
		const { container, getState } = renderForm();

		typeInColumn(container, 'Phenotype Statement', 'edited', 1);

		expect(savedRows(getState).map((row) => row.phenotypeStatement)).toEqual(['first', 'edited']);
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

	it('adds an empty inheritance mode at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getState } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Inheritance Mode' }));
		typeInColumn(container, 'Phenotype Statement', 'edited', 2);

		expect(savedRows(getState)[0]).toMatchObject({
			inheritanceMode: null,
			phenotypeTerm: null,
			phenotypeStatement: '',
			internal: false,
		});
		expect(savedRows(getState).map((row) => row.phenotypeStatement)).toEqual(EDITED_AFTER_ADD);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getState } = renderForm();
		const secondKey = savedRows(getState)[1].dataKey;

		clickDeleteInRow(container, 0);
		typeInColumn(container, 'Phenotype Statement', 'edited', 0);

		expect(savedRows(getState).map((row) => [row.dataKey, row.phenotypeStatement])).toEqual([
			[secondKey, EDITED_VALUE],
		]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getState, dispatch } = renderForm();
		const [firstKey, secondKey] = savedRows(getState).map((row) => row.dataKey);
		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'alleleInheritanceModes',
			errorMessages: { [firstKey]: { phenotypeStatement: ERROR }, [secondKey]: { phenotypeStatement: ERROR } },
		});

		clickDeleteInRow(container, 0);

		expect(getState().entityStates.alleleInheritanceModes.errorMessages).toEqual({
			[secondKey]: { phenotypeStatement: ERROR },
		});
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getState, dispatch } = renderForm();

		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'alleleInheritanceModes',
			errorMessages: { [savedRows(getState)[1].dataKey]: { phenotypeStatement: ERROR } },
		});

		expect(getCellByHeader(container, 'Phenotype Statement', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Phenotype Statement', 0).textContent).not.toContain('second row is bad');
	});
});
