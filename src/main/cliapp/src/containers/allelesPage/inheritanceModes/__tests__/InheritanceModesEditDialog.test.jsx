import { render, fireEvent, waitFor } from '@testing-library/react';
import { InheritanceModesEditDialog } from '../InheritanceModesEditDialog';
import { vocabularyTermsFor } from '../../../../service/__mocks__/useControlledVocabularyService';
import { getCellByHeader, typeInColumn, selectInColumn, clickDeleteInRow } from '../../__tests__/alleleFormTestUtils';

vi.mock('../../../../service/useControlledVocabularyService');
vi.mock('../../../../service/useVocabularyTermSetService');

// Typing a phenotype term starts a debounced autocomplete search that can fire after the test ends.
vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [] }));
	},
}));

// The dialog constructs a ValidationService at render and calls it once per row on
// "Keep Edits"; the save path cannot be exercised without standing in for it.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const makeRow = (phenotypeStatement) => ({
	inheritanceMode: null,
	phenotypeTerm: null,
	phenotypeStatement,
	internal: false,
	evidence: [],
});

const ROWS = [makeRow('stmt A'), makeRow('stmt B'), makeRow('stmt C')];

const renderDialog = async (originalInheritanceModes = ROWS) => {
	const editorCallback = vi.fn();
	const result = render(
		<InheritanceModesEditDialog
			originalInheritanceModesData={{
				originalInheritanceModes,
				isInEdit: true,
				dialog: true,
				rowIndex: 0,
				mainRowProps: { editorCallback },
			}}
			setOriginalInheritanceModesData={vi.fn()}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={vi.fn()}
		/>
	);

	// The dialog clones its rows and assigns their dataKeys in onShow, which
	// PrimeReact fires at the end of the open transition.
	await waitFor(() => {
		const rows = result.baseElement.querySelectorAll('tbody tr');
		expect(rows).toHaveLength(originalInheritanceModes.length);
		// A table with no rows renders one empty-message <tr>, which a bare count check
		// mistakes for a real row the moment a fixture has just one.
		expect(rows[0].querySelectorAll('td').length).toBeGreaterThan(1);
	});

	return { ...result, editorCallback };
};

const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

const getButton = (baseElement, label) => {
	const button = [...baseElement.querySelectorAll('button')].find(
		(candidate) => candidate.textContent.trim() === label
	);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

/** The visible selection of a row's Internal dropdown. */
const internalInRow = (baseElement, rowIndex) =>
	getCellByHeader(baseElement, 'Internal', rowIndex).querySelector('.p-dropdown-label').textContent.trim();

/** The text in a row's Phenotype Statement text area. */
const statementInRow = (baseElement, rowIndex) =>
	getCellByHeader(baseElement, 'Phenotype Statement', rowIndex).querySelector('textarea').value;

/**
 * The edit dialog owns its rows in local state, so a curator's edit is only persisted if it
 * lands on the row they clicked and survives into the payload handed to the main row's
 * editorCallback.
 *
 * Rows are addressed by dataKey, the key `editingRows` and the error map already use.
 * The delete-then-edit test below is what pins that down.
 */
describe('<InheritanceModesEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('opens with one editable row per inheritance mode', async () => {
		const { baseElement } = await renderDialog();

		expect(statementInRow(baseElement, 0)).toBe('stmt A');
		expect(statementInRow(baseElement, 1)).toBe('stmt B');
		expect(statementInRow(baseElement, 2)).toBe('stmt C');
	});

	// The header group and the body columns are declared separately, so nothing else
	// checks they agree — and a dropped column silently shifts every column after it.
	it('renders one body cell per declared header', async () => {
		const { baseElement } = await renderDialog();

		const headers = baseElement.querySelectorAll('thead tr:first-child th');
		const cells = baseElement.querySelectorAll('tbody tr')[0].querySelectorAll('td');

		expect(cells).toHaveLength(headers.length);
	});

	it('hands back typed phenotype term text keyed by curie', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		typeInColumn(baseElement, 'Phenotype Term', 'HP:0000001', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback.mock.calls.at(-1)[0][1].phenotypeTerm).toEqual({ curie: 'HP:0000001' });
	});

	it('hands the edited rows to the main row editorCallback with the dialog dataKeys stripped', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		typeInColumn(baseElement, 'Phenotype Statement', 'stmt EDITED', 1);
		selectInColumn(baseElement, 'Internal', 'true', 1);
		selectInColumn(baseElement, 'Inheritance Mode', 'allele_inheritance_mode_beta', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			makeRow('stmt A'),
			{ ...makeRow('stmt EDITED'), internal: true, inheritanceMode: vocabularyTermsFor('allele_inheritance_mode')[1] },
			makeRow('stmt C'),
		]);
	});

	// Deleting a row moves the rows after it to new positions while their dataKeys stay the
	// same. A strategy that reads a row by one and writes it by the other stores the edit
	// against, and saves it as, a different row. Before any delete both point at the same
	// row, so only this order exposes it.
	it('edits the row the curator clicked after an earlier row was deleted', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		clickDeleteInRow(baseElement, 0);

		await waitFor(() => expect(baseElement.querySelectorAll('tbody tr')).toHaveLength(2));
		expect(statementInRow(baseElement, 0)).toBe('stmt B');
		expect(statementInRow(baseElement, 1)).toBe('stmt C');

		// Internal is a controlled dropdown, so it only shows the new selection if the
		// write reached the row that was clicked.
		selectInColumn(baseElement, 'Internal', 'true', 1);

		expect(internalInRow(baseElement, 1)).toBe('true');
		expect(internalInRow(baseElement, 0)).toBe('false');

		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([makeRow('stmt B'), { ...makeRow('stmt C'), internal: true }]);
	});

	it('renders a validation error against the row it was keyed to, and does not save', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		validate.mockImplementation((endpoint, inheritanceMode) =>
			Promise.resolve(
				inheritanceMode.phenotypeStatement === 'stmt B'
					? { isSuccess: false, isError: true, data: { phenotypeStatement: 'must be unique' } }
					: { isSuccess: true, isError: false, data: {} }
			)
		);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Phenotype Statement', 1).textContent).toContain('must be unique')
		);
		expect(getCellByHeader(baseElement, 'Phenotype Statement', 0).textContent).not.toContain('must be unique');
		expect(editorCallback).not.toHaveBeenCalled();
	});
});
