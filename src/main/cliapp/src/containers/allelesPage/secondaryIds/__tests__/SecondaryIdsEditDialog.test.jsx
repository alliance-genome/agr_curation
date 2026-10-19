import { render, fireEvent, waitFor } from '@testing-library/react';
import { SecondaryIdsEditDialog } from '../SecondaryIdsEditDialog';
import { getCellByHeader, typeInColumn, selectInColumn, clickDeleteInRow } from '../../__tests__/alleleFormTestUtils';

// The dialog constructs a ValidationService at render and calls it once per row on
// "Keep Edits"; the save path cannot be exercised without standing in for it.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const ROWS = [
	{ secondaryId: 'AGRKB:A', internal: false, evidence: [] },
	{ secondaryId: 'AGRKB:B', internal: false, evidence: [] },
	{ secondaryId: 'AGRKB:C', internal: false, evidence: [] },
];

const renderDialog = async (
	originalSecondaryIds = ROWS,
	{ field = 'alleleSecondaryIds', endpoint = 'allelesecondaryidslotannotation' } = {}
) => {
	const editorCallback = vi.fn();
	const setErrorMessagesMainRow = vi.fn();
	const result = render(
		<SecondaryIdsEditDialog
			field={field}
			endpoint={endpoint}
			originalSecondaryIdsData={{
				originalSecondaryIds,
				isInEdit: true,
				dialog: true,
				rowIndex: 0,
				mainRowProps: { editorCallback },
			}}
			setOriginalSecondaryIdsData={vi.fn()}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={setErrorMessagesMainRow}
		/>
	);

	// The dialog clones its rows and assigns their dataKeys in onShow, which
	// PrimeReact fires at the end of the open transition.
	await waitFor(() => {
		const rows = result.baseElement.querySelectorAll('tbody tr');
		expect(rows).toHaveLength(originalSecondaryIds.length);
		// A table with no rows renders one empty-message <tr>, which a bare count check
		// mistakes for a real row the moment a fixture has just one.
		expect(rows[0].querySelectorAll('td').length).toBeGreaterThan(1);
	});

	return { ...result, editorCallback, setErrorMessagesMainRow };
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

/** The text in a row's Secondary ID input. */
const secondaryIdInRow = (baseElement, rowIndex) =>
	getCellByHeader(baseElement, 'Secondary ID', rowIndex).querySelector('input').value;

/**
 * The edit dialog owns its rows in local state, so a curator's edit is only persisted if it
 * lands on the row they clicked and survives into the payload handed to the main row's
 * editorCallback.
 *
 * Rows are addressed by dataKey, the key `editingRows` and the error map already use.
 * The delete-then-edit test below is what pins that down.
 */
describe('<SecondaryIdsEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('opens with one editable row per secondary id', async () => {
		const { baseElement } = await renderDialog();

		expect(secondaryIdInRow(baseElement, 0)).toBe('AGRKB:A');
		expect(secondaryIdInRow(baseElement, 1)).toBe('AGRKB:B');
		expect(secondaryIdInRow(baseElement, 2)).toBe('AGRKB:C');
	});

	// The Alleles, Genes and Affected Genomic Models tables share this dialog, each with its
	// own field and endpoint.
	it('validates against its endpoint and files the pending-edit warning under its field', async () => {
		const { baseElement, setErrorMessagesMainRow } = await renderDialog(ROWS, {
			field: 'agmSecondaryIds',
			endpoint: 'agmsecondaryidslotannotation',
		});

		keepEdits(baseElement);

		await waitFor(() => expect(setErrorMessagesMainRow).toHaveBeenCalled());
		expect(validate).toHaveBeenCalledWith('agmsecondaryidslotannotation', expect.anything());
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			0: { agmSecondaryIds: { severity: 'warn', message: 'Pending Edits!' } },
		});
	});

	it('hands the edited rows to the main row editorCallback with the dialog dataKeys stripped', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		typeInColumn(baseElement, 'Secondary ID', 'AGRKB:EDITED', 1);
		selectInColumn(baseElement, 'Internal', 'true', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ secondaryId: 'AGRKB:A', internal: false, evidence: [] },
			{ secondaryId: 'AGRKB:EDITED', internal: true, evidence: [] },
			{ secondaryId: 'AGRKB:C', internal: false, evidence: [] },
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
		expect(secondaryIdInRow(baseElement, 0)).toBe('AGRKB:B');
		expect(secondaryIdInRow(baseElement, 1)).toBe('AGRKB:C');

		// Internal is a controlled dropdown, so it only shows the new selection if the
		// write reached the row that was clicked.
		selectInColumn(baseElement, 'Internal', 'true', 1);

		expect(internalInRow(baseElement, 1)).toBe('true');
		expect(internalInRow(baseElement, 0)).toBe('false');

		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ secondaryId: 'AGRKB:B', internal: false, evidence: [] },
			{ secondaryId: 'AGRKB:C', internal: true, evidence: [] },
		]);
	});

	it('renders a validation error against the row it was keyed to, and does not save', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		validate.mockImplementation((endpoint, secondaryId) =>
			Promise.resolve(
				secondaryId.secondaryId === 'AGRKB:B'
					? { isSuccess: false, isError: true, data: { secondaryId: 'must be unique' } }
					: { isSuccess: true, isError: false, data: {} }
			)
		);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Secondary ID', 1).textContent).toContain('must be unique')
		);
		expect(getCellByHeader(baseElement, 'Secondary ID', 0).textContent).not.toContain('must be unique');
		expect(editorCallback).not.toHaveBeenCalled();
	});
});
