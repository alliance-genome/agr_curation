import { fireEvent } from '@testing-library/react';
import { NomenclatureEventsForm } from '../NomenclatureEventsForm';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	removeFirstTokenInColumn,
	renderSubResourceForm,
	selectInColumn,
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
const [ALPHA, BETA] = vocabularyTermsFor('allele_nomenclature_event');

const nomenclatureEvent = (term) => ({ nomenclatureEvent: term, internal: false, obsolete: false, evidence: EVIDENCE });

const renderForm = () =>
	renderSubResourceForm(NomenclatureEventsForm, 'alleleNomenclatureEvents', [
		nomenclatureEvent(ALPHA),
		nomenclatureEvent(ALPHA),
	]);

const EDITED_VALUE = BETA;
const EDITED_AFTER_ADD = [null, ALPHA, BETA];

describe('<NomenclatureEventsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE] }));
	});

	it('saves the whole nomenclature event term on the row it was picked in', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Nomenclature Event', 'allele_nomenclature_event_beta', 1);

		expect(getRows().map((row) => row.nomenclatureEvent)).toEqual([ALPHA, BETA]);
	});

	it('saves obsolete as a boolean', () => {
		const { container, getRows } = renderForm();

		selectInColumn(container, 'Obsolete', 'true', 1);

		expect(getRows().map((row) => row.obsolete)).toEqual([false, true]);
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

	it('adds an empty nomenclature event at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getRows } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Nomenclature Event' }));
		selectInColumn(container, 'Nomenclature Event', 'allele_nomenclature_event_beta', 2);

		expect(getRows()[0]).toMatchObject({ nomenclatureEvent: null, internal: false, obsolete: false });
		expect(getRows().map((row) => row.nomenclatureEvent)).toEqual(EDITED_AFTER_ADD);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getRows } = renderForm();
		const secondKey = getRows()[1].dataKey;

		clickDeleteInRow(container, 0);
		selectInColumn(container, 'Nomenclature Event', 'allele_nomenclature_event_beta', 0);

		expect(getRows().map((row) => [row.dataKey, row.nomenclatureEvent])).toEqual([[secondKey, EDITED_VALUE]]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getRows, getErrorMessages, setErrorMessages } = renderForm();
		const [firstKey, secondKey] = getRows().map((row) => row.dataKey);
		setErrorMessages({ [firstKey]: { nomenclatureEvent: ERROR }, [secondKey]: { nomenclatureEvent: ERROR } });

		clickDeleteInRow(container, 0);

		expect(getErrorMessages()).toEqual({
			[secondKey]: { nomenclatureEvent: ERROR },
		});
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { nomenclatureEvent: ERROR } });

		expect(getCellByHeader(container, 'Nomenclature Event', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Nomenclature Event', 0).textContent).not.toContain('second row is bad');
	});
});
