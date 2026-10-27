import { fireEvent } from '@testing-library/react';
import { SecondaryIdsForm } from '../SecondaryIdsForm';
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

const secondaryId = (id) => ({ secondaryId: id, internal: false, evidence: EVIDENCE });

const renderForm = () =>
	renderSubResourceForm(SecondaryIdsForm, 'alleleSecondaryIds', [secondaryId('WB:1'), secondaryId('WB:2')]);

const EDITED_VALUE = 'WB:9';
const EDITED_AFTER_ADD = ['', 'WB:1', 'WB:9'];

describe('<SecondaryIdsForm />', () => {
	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE] }));
	});

	it('saves a typed secondary ID on the row it was typed in', () => {
		const { container, getRows } = renderForm();

		typeInColumn(container, 'Secondary ID', 'WB:9', 1);

		expect(getRows().map((row) => row.secondaryId)).toEqual(['WB:1', 'WB:9']);
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

	it('adds an empty secondary ID at the top and saves an edit on the right row after it', () => {
		const { container, getByRole, getRows } = renderForm();

		fireEvent.click(getByRole('button', { name: 'Add Secondary ID' }));
		typeInColumn(container, 'Secondary ID', 'WB:9', 2);

		expect(getRows()[0]).toMatchObject({ secondaryId: '', internal: false });
		expect(getRows().map((row) => row.secondaryId)).toEqual(EDITED_AFTER_ADD);
	});

	it('saves an edit on the row that moved up after the row above it is deleted', () => {
		const { container, getRows } = renderForm();
		const secondKey = getRows()[1].dataKey;

		clickDeleteInRow(container, 0);
		typeInColumn(container, 'Secondary ID', 'WB:9', 0);

		expect(getRows().map((row) => [row.dataKey, row.secondaryId])).toEqual([[secondKey, EDITED_VALUE]]);
	});

	it("drops a deleted row's errors and keeps the others", () => {
		const { container, getRows, getErrorMessages, setErrorMessages } = renderForm();
		const [firstKey, secondKey] = getRows().map((row) => row.dataKey);
		setErrorMessages({ [firstKey]: { secondaryId: ERROR }, [secondKey]: { secondaryId: ERROR } });

		clickDeleteInRow(container, 0);

		expect(getErrorMessages()).toEqual({ [secondKey]: { secondaryId: ERROR } });
	});

	it('shows a row error on the row whose key it is stored under', () => {
		const { container, getRows, setErrorMessages } = renderForm();

		setErrorMessages({ [getRows()[1].dataKey]: { secondaryId: ERROR } });

		expect(getCellByHeader(container, 'Secondary ID', 1).textContent).toContain('second row is bad');
		expect(getCellByHeader(container, 'Secondary ID', 0).textContent).not.toContain('second row is bad');
	});
});
