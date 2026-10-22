import { render, fireEvent, waitFor } from '@testing-library/react';
import { NomenclatureEventsEditDialog } from '../NomenclatureEventsEditDialog';
import { vocabularyTermsFor } from '../../../../service/__mocks__/useControlledVocabularyService';
import { getCellByHeader, selectInColumn, clickDeleteInRow } from '../../__tests__/alleleFormTestUtils';

vi.mock('../../../../service/useControlledVocabularyService');

// The dialog constructs a ValidationService at render and calls it once per row on
// "Keep Edits"; the save path cannot be exercised without standing in for it.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const makeRow = (nomenclatureEvent) => ({
	nomenclatureEvent,
	internal: false,
	obsolete: false,
	evidence: [],
});

const [ALPHA, BETA] = vocabularyTermsFor('allele_nomenclature_event');

const ROWS = [makeRow(ALPHA), makeRow(null), makeRow(BETA)];

const renderDialog = async (originalNomenclatureEvents = ROWS) => {
	const editorCallback = vi.fn();
	const result = render(
		<NomenclatureEventsEditDialog
			originalNomenclatureEventsData={{
				originalNomenclatureEvents,
				isInEdit: true,
				dialog: true,
				rowIndex: 0,
				mainRowProps: { editorCallback },
			}}
			setOriginalNomenclatureEventsData={vi.fn()}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={vi.fn()}
		/>
	);

	// The dialog clones its rows and assigns their dataKeys in onShow, which
	// PrimeReact fires at the end of the open transition.
	await waitFor(() => {
		const rows = result.baseElement.querySelectorAll('tbody tr');
		expect(rows).toHaveLength(originalNomenclatureEvents.length);
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

/** The visible selection of a row's dropdown in `header`. */
const selectionInRow = (baseElement, header, rowIndex) =>
	getCellByHeader(baseElement, header, rowIndex).querySelector('.p-dropdown-label').textContent.trim();

/**
 * The edit dialog owns its rows in local state, so a curator's edit is only persisted if it
 * lands on the row they clicked and survives into the payload handed to the main row's
 * editorCallback.
 *
 * Rows are addressed by dataKey, the key `editingRows` and the error map already use.
 * The delete-then-edit test below is what pins that down.
 */
describe('<NomenclatureEventsEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('opens with one editable row per nomenclature event', async () => {
		const { baseElement } = await renderDialog();

		expect(selectionInRow(baseElement, 'Nomenclature Event', 0)).toBe(ALPHA.name);
		expect(selectionInRow(baseElement, 'Nomenclature Event', 2)).toBe(BETA.name);
	});

	// The header group and the body columns are declared separately, so nothing else
	// checks they agree — and a dropped column silently shifts every column after it.
	it('renders one body cell per declared header', async () => {
		const { baseElement } = await renderDialog();

		const headers = baseElement.querySelectorAll('thead tr:first-child th');
		const cells = baseElement.querySelectorAll('tbody tr')[0].querySelectorAll('td');

		expect(cells).toHaveLength(headers.length);
	});

	// Two boolean columns side by side: a widget bound to the wrong field would write
	// internal here and go unnoticed anywhere else in the suite.
	it('keeps the two boolean columns on their own fields', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		selectInColumn(baseElement, 'Obsolete', 'true', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([ROWS[0], { ...makeRow(null), obsolete: true }, ROWS[2]]);
	});

	it('hands the edited rows to the main row editorCallback with the dialog dataKeys stripped', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		selectInColumn(baseElement, 'Nomenclature Event', BETA.name, 1);
		selectInColumn(baseElement, 'Internal', 'true', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([ROWS[0], { ...makeRow(BETA), internal: true }, ROWS[2]]);
	});

	// Deleting a row moves the rows after it to new positions while their dataKeys stay the
	// same. A strategy that reads a row by one and writes it by the other stores the edit
	// against, and saves it as, a different row. Before any delete both point at the same
	// row, so only this order exposes it.
	it('edits the row the curator clicked after an earlier row was deleted', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		clickDeleteInRow(baseElement, 0);

		await waitFor(() => expect(baseElement.querySelectorAll('tbody tr')).toHaveLength(2));

		// Internal is a controlled dropdown, so it only shows the new selection if the
		// write reached the row that was clicked.
		selectInColumn(baseElement, 'Internal', 'true', 1);

		expect(selectionInRow(baseElement, 'Internal', 1)).toBe('true');
		expect(selectionInRow(baseElement, 'Internal', 0)).toBe('false');

		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([ROWS[1], { ...ROWS[2], internal: true }]);
	});

	it('renders a validation error against the row it was keyed to, and does not save', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		validate.mockImplementation((endpoint, nomenclatureEvent) =>
			Promise.resolve(
				nomenclatureEvent.nomenclatureEvent === null
					? { isSuccess: false, isError: true, data: { nomenclatureEvent: 'Required field is empty' } }
					: { isSuccess: true, isError: false, data: {} }
			)
		);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Nomenclature Event', 1).textContent).toContain('Required field is empty')
		);
		expect(getCellByHeader(baseElement, 'Nomenclature Event', 0).textContent).not.toContain('Required field is empty');
		expect(editorCallback).not.toHaveBeenCalled();
	});
});
