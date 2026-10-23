import { fireEvent } from '@testing-library/react';
import { FunctionalImpactsForm } from '../FunctionalImpactsForm';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	renderSubResourceForm,
	selectInColumn,
	typeInColumn,
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
const IMPACTS = [
	{ id: 21, name: 'amorph' },
	{ id: 22, name: 'hypomorph' },
];
const NEW_IMPACT = { id: 23, name: 'hypermorph' };
const PHENOTYPE_TERM = { id: 31, curie: 'HP:0000001', name: 'All' };

const functionalImpact = (statement) => ({
	functionalImpacts: IMPACTS,
	phenotypeTerm: null,
	phenotypeStatement: statement,
	internal: false,
	evidence: EVIDENCE,
});

const renderForm = () =>
	renderSubResourceForm(FunctionalImpactsForm, 'alleleFunctionalImpacts', [
		functionalImpact('first'),
		functionalImpact('second'),
	]);

const RESULTS_BY_ENDPOINT = { vocabularyterm: NEW_IMPACT, phenotypeterm: PHENOTYPE_TERM };
const EDITED_VALUE = 'edited';
const EDITED_AFTER_ADD = ['', 'first', 'edited'];

describe('<FunctionalImpactsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn((endpoint) =>
			Promise.resolve({ results: [RESULTS_BY_ENDPOINT[endpoint] ?? NEW_EVIDENCE] })
		);
	});

	it('saves a picked functional impact alongside the existing ones', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Functional Impacts', 'hyper', /hypermorph/, 1);

		expect(getRows().map((row) => row.functionalImpacts)).toEqual([IMPACTS, [...IMPACTS, NEW_IMPACT]]);
	});

	it('saves the remaining functional impacts on the row a token was removed from', () => {
		const { container, getRows } = renderForm();

		removeFirstTokenInColumn(container, 'Functional Impacts', 1);

		expect(getRows().map((row) => row.functionalImpacts)).toEqual([IMPACTS, [IMPACTS[1]]]);
	});

	it('saves a picked phenotype term on the row it was picked in', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Phenotype Term', 'HP', /HP:0000001/, 1);

		expect(getRows().map((row) => row.phenotypeTerm)).toEqual([null, PHENOTYPE_TERM]);
	});

	it('saves a typed phenotype statement on the row it was typed in', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Phenotype Statement', 'edited', 1);

		expect(getRows().map((row) => row.phenotypeStatement)).toEqual(['first', 'edited']);
	});

	it('saves a picked evidence alongside the existing ones', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/, 1);

		expect(getRows().map((row) => row.evidence)).toEqual([EVIDENCE, [...EVIDENCE, NEW_EVIDENCE]]);
	});

	it('saves the remaining evidence when a token is removed', () => {
		const { container, getRows } = renderForm();

		removeFirstTokenInColumn(container, 'Evidence', 1);

		expect(getRows().map((row) => row.evidence)).toEqual([EVIDENCE, [EVIDENCE[1]]]);
	});

	it('saves internal as a boolean', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Internal', 'true', 1);

		expect(getRows().map((row) => row.internal)).toEqual([false, true]);
	});

	it('adds an empty functional impact at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getRows } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Functional Impact' }));
		typeInColumn(container, 'Phenotype Statement', 'edited', 2);

		expect(getRows()[0]).toMatchObject({ phenotypeStatement: '', internal: false });
		expect(getRows().map((row) => row.phenotypeStatement)).toEqual(EDITED_AFTER_ADD);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getRows } = renderForm();
		const secondKey = getRows()[1].dataKey;

		clickDeleteInRow(container, 0);
		typeInColumn(container, 'Phenotype Statement', 'edited', 0);

		expect(getRows().map((row) => [row.dataKey, row.phenotypeStatement])).toEqual([[secondKey, EDITED_VALUE]]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getRows, getErrorMessages, setErrorMessages } = renderForm();
		const [firstKey, secondKey] = getRows().map((row) => row.dataKey);
		setErrorMessages({ [firstKey]: { phenotypeStatement: ERROR }, [secondKey]: { phenotypeStatement: ERROR } });

		clickDeleteInRow(container, 0);

		expect(getErrorMessages()).toEqual({
			[secondKey]: { phenotypeStatement: ERROR },
		});
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { phenotypeStatement: ERROR } });

		expect(getCellByHeader(container, 'Phenotype Statement', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Phenotype Statement', 0).textContent).not.toContain('second row is bad');
	});
});
