import { fireEvent, render, waitFor } from '@testing-library/react';
import { ConditionRelationsEditDialog } from '../ConditionRelationsEditDialog';
import { vocabularyTermsFor } from '../../service/__mocks__/useControlledVocabularyService';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	selectInColumn,
	typeInColumn,
} from '../../containers/allelesPage/__tests__/alleleFormTestUtils';
import { SearchService } from '../../service/SearchService';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../constants/FilterFields';

vi.mock('../../service/SearchService');
vi.mock('../../service/useControlledVocabularyService');
vi.mock('../../service/useVocabularyTermSetService');

// The pop-up constructs a ValidationService at render and calls it once per relation on Keep Edits.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const RELATION_VOCABULARY = 'condition_relation';
const CONDITION = { id: 61, conditionSummary: 'heat shock' };
const NEW_CONDITION = { id: 62, conditionSummary: 'cold exposure' };
const VALID = { isSuccess: true, isError: false, data: {} };

const relation = (overrides = {}) => ({
	conditionRelationType: vocabularyTermsFor(RELATION_VOCABULARY)[0],
	conditions: [CONDITION],
	internal: false,
	...overrides,
});

const getButton = (baseElement, label) => {
	const button = [...baseElement.querySelectorAll('button')].find(
		(candidate) => candidate.textContent.trim() === label
	);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

const dropdownLabel = (container, header, rowIndex = 0) =>
	getCellByHeader(container, header, rowIndex).querySelector('.p-dropdown-label').textContent.trim();

const tokens = (container, header, rowIndex = 0) =>
	[...getCellByHeader(container, header, rowIndex).querySelectorAll('.p-autocomplete-token-label')].map((token) =>
		token.textContent.trim()
	);

const bodyRows = (baseElement) =>
	[...baseElement.querySelectorAll('.p-dialog tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

/** Picks a condition suggestion and waits for its panel to finish closing, which it does after the pick. */
const pickCondition = async (container, rowIndex = 0) => {
	await pickSuggestionInColumn(container, 'Conditions', 'cold', /cold exposure/, rowIndex);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

/**
 * The condition relations pop-up the Disease Annotations table opens. It edits a row's condition relations in
 * local state and hands them back to the main row on Keep Edits, once every relation passes validation.
 */
describe('<ConditionRelationsEditDialog />', () => {
	const openDialog = async (originalConditionRelations = [relation()]) => {
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		const result = render(
			<ConditionRelationsEditDialog
				originalConditionRelationsData={{
					originalConditionRelations,
					isInEdit: true,
					dialog: true,
					rowIndex: 3,
					mainRowProps: { editorCallback },
				}}
				setOriginalConditionRelationsData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={setErrorMessagesMainRow}
			/>
		);
		// The pop-up copies its relations in onShow, which runs as the open transition ends.
		await waitFor(() => {
			expect(result.baseElement.querySelector('.p-dialog-enter-done')).not.toBeNull();
			expect(bodyRows(result.baseElement)).toHaveLength(originalConditionRelations.length);
		});
		return { ...result, editorCallback, setErrorMessagesMainRow };
	};

	const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_CONDITION] }));
		validate.mockReset();
		validate.mockResolvedValue(VALID);
	});

	it("opens with the row's condition relations", async () => {
		const { baseElement } = await openDialog([relation(), relation({ internal: true, conditions: [] })]);

		expect(baseElement.querySelector('.p-dialog h3').textContent).toBe('Experimental Conditions');
		expect(dropdownLabel(baseElement, 'Relation')).toBe(`${RELATION_VOCABULARY}_alpha`);
		expect(tokens(baseElement, 'Conditions')).toEqual(['heat shock']);
		expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
		expect(tokens(baseElement, 'Conditions', 1)).toEqual([]);
		expect(dropdownLabel(baseElement, 'Internal', 1)).toBe('true');
	});

	it('offers the condition relation types', async () => {
		const { baseElement } = await openDialog();

		fireEvent.click(getCellByHeader(baseElement, 'Relation').querySelector('.p-dropdown'));

		const options = [...document.querySelectorAll('.p-dropdown-item')].map((item) => item.textContent.trim());
		expect(options).toEqual([`${RELATION_VOCABULARY}_alpha`, `${RELATION_VOCABULARY}_beta`]);
	});

	// Conditions are found by their chemical, anatomy and other terms as well as their summary.
	it('searches every condition field set', async () => {
		const { baseElement } = await openDialog();

		typeInColumn(baseElement, 'Conditions', 'cold');

		await waitFor(() => expect(SearchService.prototype.search).toHaveBeenCalled(), { timeout: 3000 });
		const [endpoint, , , , params] = SearchService.prototype.search.mock.calls.at(-1);
		expect(endpoint).toBe('experimental-condition');
		expect(Object.keys(params)).toEqual(['conditionSummaryFilter', 'obsoleteFilter']);
		expect(Object.keys(params.conditionSummaryFilter)).toEqual(
			getAutocompleteFields(AUTOCOMPLETE_CONFIGS.experimentalConditionDetailedAutocompleteConfig)
		);
	});

	it('hands back the edited relations, each validated, and marks the row as having pending edits', async () => {
		const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog();

		selectInColumn(baseElement, 'Relation', `${RELATION_VOCABULARY}_beta`);
		await pickCondition(baseElement);
		selectInColumn(baseElement, 'Internal', 'true');
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const edited = {
			conditionRelationType: vocabularyTermsFor(RELATION_VOCABULARY)[1],
			conditions: [CONDITION, NEW_CONDITION],
			internal: true,
		};
		expect(editorCallback).toHaveBeenCalledWith([edited]);
		expect(validate).toHaveBeenCalledTimes(1);
		expect(validate).toHaveBeenCalledWith('condition-relation', edited);
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			3: { conditionRelations: { severity: 'warn', message: 'Pending Edits!' } },
		});
	});

	it('hands back a new relation added with New Condition', async () => {
		const { baseElement, editorCallback } = await openDialog();

		fireEvent.click(getButton(baseElement, 'New Condition'));
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(2));
		selectInColumn(baseElement, 'Relation', `${RELATION_VOCABULARY}_beta`, 1);
		await pickCondition(baseElement, 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const handedBack = editorCallback.mock.calls[0][0];
		expect(handedBack).toHaveLength(2);
		expect(handedBack[0]).toEqual(relation());
		expect(handedBack[1]).toMatchObject({
			conditionRelationType: vocabularyTermsFor(RELATION_VOCABULARY)[1],
			conditions: [NEW_CONDITION],
			internal: false,
		});
		expect(handedBack[1]).not.toHaveProperty('dataKey');
	});

	it('saves each edit on the relation that moved up after the relation above it is deleted', async () => {
		const { baseElement, editorCallback } = await openDialog([relation(), relation({ conditions: [] })]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		selectInColumn(baseElement, 'Relation', `${RELATION_VOCABULARY}_beta`);
		await pickCondition(baseElement);
		selectInColumn(baseElement, 'Internal', 'true');
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{
				conditionRelationType: vocabularyTermsFor(RELATION_VOCABULARY)[1],
				conditions: [NEW_CONDITION],
				internal: true,
			},
		]);
	});

	it('shows a validation error on the relation that caused it and keeps the edits back', async () => {
		validate
			.mockResolvedValueOnce(VALID)
			.mockResolvedValueOnce({ isSuccess: false, isError: true, data: { conditions: 'second relation is bad' } });
		const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog([relation(), relation()]);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Conditions', 1).textContent).toContain('second relation is bad')
		);
		expect(getCellByHeader(baseElement, 'Conditions', 0).textContent).not.toContain('second relation is bad');
		expect(editorCallback).not.toHaveBeenCalled();
		expect(setErrorMessagesMainRow).not.toHaveBeenCalled();
	});

	it('shows a validation error on the right relation after the relation above it is deleted', async () => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: { conditions: 'remaining relation is bad' } });
		const { baseElement } = await openDialog([relation(), relation({ internal: true })]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Conditions').textContent).toContain('remaining relation is bad')
		);
		expect(dropdownLabel(baseElement, 'Internal')).toBe('true');
	});

	it('shows each validation error on its own cell of the relation that caused it', async () => {
		const columnErrors = [
			['Relation', 'conditionRelationType', 'relation is bad'],
			['Conditions', 'conditions', 'conditions are bad'],
			['Internal', 'internal', 'internal is bad'],
		];
		validate.mockResolvedValueOnce(VALID).mockResolvedValueOnce({
			isSuccess: false,
			isError: true,
			data: Object.fromEntries(columnErrors.map(([, field, message]) => [field, message])),
		});
		const { baseElement } = await openDialog([relation(), relation()]);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Conditions', 1).textContent).toContain('conditions are bad')
		);
		columnErrors.forEach(([header, , message]) => {
			expect(getCellByHeader(baseElement, header, 1).textContent).toContain(message);
			expect(getCellByHeader(baseElement, header, 0).textContent).not.toContain(message);
			columnErrors
				.filter(([otherHeader]) => otherHeader !== header)
				.forEach(([, , otherMessage]) =>
					expect(getCellByHeader(baseElement, header, 1).textContent).not.toContain(otherMessage)
				);
		});
	});
});
