import { fireEvent } from '@testing-library/react';
import { SynonymsForm } from '../SynonymsForm';
import {
	clearInColumn,
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	renderSubResourceForm,
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

const synonym = (displayText) => ({
	displayText,
	formatText: displayText,
	synonymScope: vocabularyTermsFor('synonym_scope')[1],
	nameType: null,
	synonymUrl: '',
	internal: false,
	evidence: EVIDENCE,
});

const renderForm = () => renderSubResourceForm(SynonymsForm, 'alleleSynonyms', [synonym('first'), synonym('second')]);

describe('<SynonymsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE] }));
	});

	it('saves typed display text on the row it was typed in', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Display Text', 'edited', 1);

		expect(getRows().map((row) => row.displayText)).toEqual(['first', 'edited']);
	});

	it('saves typed format text and synonym URL', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Format Text', 'edited', 1);
		typeInColumn(container, 'Synonym URL', 'https://example.org', 1);

		expect(getRows()[1]).toMatchObject({ formatText: 'edited', synonymUrl: 'https://example.org' });
		expect(getRows()[0]).toMatchObject({ formatText: 'first', synonymUrl: '' });
	});

	it('saves the whole synonym scope term', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Synonym Scope', 'synonym_scope_alpha', 1);

		const [alpha, beta] = vocabularyTermsFor('synonym_scope');
		expect(getRows().map((row) => row.synonymScope)).toEqual([beta, alpha]);
	});

	it('saves no synonym scope when it is cleared', () => {
		const { container, getRows } = renderForm();

		clearInColumn(container, 'Synonym Scope', 1);

		// An absent scope and a null scope both save as no scope.
		expect(getRows()[1].synonymScope ?? null).toBeNull();
		expect(getRows()[0].synonymScope).toEqual(vocabularyTermsFor('synonym_scope')[1]);
	});

	it('saves the whole name type term', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Name Type', 'name_type_beta', 1);

		expect(getRows().map((row) => row.nameType)).toEqual([null, vocabularyTermsFor('name_type')[1]]);
	});

	it('saves internal as a boolean', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Internal', 'true', 1);

		expect(getRows().map((row) => row.internal)).toEqual([false, true]);
	});

	it('saves a picked evidence alongside the existing ones', async () => {
		const { container, getRows } = renderForm();

		await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/, 1);

		expect(getRows()[1].evidence).toEqual([...EVIDENCE, NEW_EVIDENCE]);
		expect(getRows()[0].evidence).toEqual(EVIDENCE);
	});

	it('saves the remaining evidence when a token is removed', () => {
		const { container, getRows } = renderForm();

		removeFirstTokenInColumn(container, 'Evidence', 1);

		expect(getRows().map((row) => row.evidence)).toEqual([EVIDENCE, [EVIDENCE[1]]]);
	});

	it('adds an empty synonym at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getRows } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Synonym' }));
		typeInColumn(container, 'Display Text', 'edited', 2);

		expect(getRows()[0]).toMatchObject({
			displayText: '',
			formatText: '',
			synonymUrl: '',
			nameType: null,
			internal: false,
			obsolete: false,
		});
		expect(getRows().map((row) => row.displayText)).toEqual(['', 'first', 'edited']);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getRows } = renderForm();
		const secondKey = getRows()[1].dataKey;

		clickDeleteInRow(container, 0);
		typeInColumn(container, 'Display Text', 'edited', 0);

		expect(getRows().map((row) => [row.dataKey, row.displayText])).toEqual([[secondKey, 'edited']]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getRows, getErrorMessages, setErrorMessages } = renderForm();
		const [firstKey, secondKey] = getRows().map((row) => row.dataKey);
		setErrorMessages({ [firstKey]: { displayText: ERROR }, [secondKey]: { displayText: ERROR } });

		clickDeleteInRow(container, 0);

		expect(getErrorMessages()).toEqual({ [secondKey]: { displayText: ERROR } });
	});

	it('shows an evidence error on its row', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { evidence: ERROR } });

		expect(getCellByHeader(container, 'Evidence', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Evidence', 0).textContent).not.toContain('second row is bad');
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { displayText: ERROR } });

		expect(getCellByHeader(container, 'Display Text', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Display Text', 0).textContent).not.toContain('second row is bad');
	});
});
