import { render, fireEvent, waitFor } from '@testing-library/react';
import { MutationTypesEditDialog } from '../MutationTypesEditDialog';

/**
 * Rows are addressed by dataKey, a per-row UUID assigned when the dialog opens. Validation
 * errors are filed under that same key, so the error case below is what distinguishes dataKey
 * addressing from positional addressing: a strategy that looks errors up by index finds
 * nothing under a UUID and renders no message.
 */

vi.mock('../../../../service/useControlledVocabularyService');
vi.mock('../../../../service/useVocabularyTermSetService');

// The dialog constructs a ValidationService at render and calls it once per row on Keep
// Edits; nothing is handed back to the main table until every row validates.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const ROWS = [
	{ id: 1, internal: false, mutationTypes: [{ curie: 'SO:0001', name: 'deletion' }], evidence: [] },
	{ id: 2, internal: false, mutationTypes: [{ curie: 'SO:0002', name: 'insertion' }], evidence: [] },
];

const renderDialog = (rows = ROWS) => {
	const editorCallback = vi.fn();
	const setOriginalMutationTypesData = vi.fn();
	const setErrorMessagesMainRow = vi.fn();
	const result = render(
		<MutationTypesEditDialog
			originalMutationTypesData={{
				originalMutationTypes: structuredClone(rows),
				isInEdit: true,
				dialog: true,
				rowIndex: 0,
				mainRowProps: { editorCallback },
			}}
			setOriginalMutationTypesData={setOriginalMutationTypesData}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={setErrorMessagesMainRow}
		/>
	);
	return { ...result, editorCallback, setOriginalMutationTypesData };
};

/**
 * The dialog clones its rows and assigns their dataKeys in onShow, which PrimeReact fires at
 * the end of the open transition.
 */
const openDialog = async (rows = ROWS) => {
	const result = renderDialog(rows);
	await waitFor(() => {
		const bodyRowNodes = result.baseElement.querySelectorAll('tbody tr');
		expect(bodyRowNodes).toHaveLength(rows.length);
		// A table with no rows renders one empty-message <tr>, which a bare count check
		// mistakes for a real row the moment a fixture has just one.
		expect(bodyRowNodes[0].querySelectorAll('td').length).toBeGreaterThan(1);
	});
	return result;
};

const bodyRows = () => [...document.querySelectorAll('.p-dialog tbody tr')];

const cellByHeader = (rowIndex, headerText) => {
	const headers = [...document.querySelectorAll('.p-dialog thead tr:first-child th')];
	const columnIndex = headers.findIndex((th) => th.textContent.trim() === headerText);
	if (columnIndex === -1) throw new Error(`No column "${headerText}"`);
	return bodyRows()[rowIndex].querySelectorAll('td')[columnIndex];
};

const getButton = (label) => {
	const button = [...document.querySelectorAll('.p-dialog button')].find(
		(candidate) => candidate.textContent.trim() === label
	);
	if (!button) {
		const seen = [...document.querySelectorAll('.p-dialog button')]
			.map((candidate) => `"${candidate.textContent.trim()}"`)
			.join(', ');
		throw new Error(`No button labelled "${label}". Saw: ${seen}`);
	}
	return button;
};

/** Keep Edits hands the rows back to the main table through the editorCallback. */
const keepEdits = () => fireEvent.click(getButton('Keep Edits'));

describe('<MutationTypesEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('renders a row per mutation type', async () => {
		await openDialog();

		expect(bodyRows()).toHaveLength(2);
		expect(cellByHeader(0, 'Mutation Types').textContent).toContain('SO:0001');
		expect(cellByHeader(1, 'Mutation Types').textContent).toContain('SO:0002');
	});

	it('labels mutation type tokens with the term name and identifier', async () => {
		await openDialog();

		expect(cellByHeader(0, 'Mutation Types').querySelector('.p-autocomplete-token-label')).toHaveTextContent(
			'deletion (SO:0001)'
		);
	});

	it('adds a row', async () => {
		await openDialog();

		fireEvent.click(getButton('New Mutation Type'));

		expect(bodyRows()).toHaveLength(3);
	});

	it('deletes the row it was asked to delete, not its neighbour', async () => {
		await openDialog();

		fireEvent.click(bodyRows()[0].querySelector('button'));

		expect(bodyRows()).toHaveLength(1);
		expect(cellByHeader(0, 'Mutation Types').textContent).toContain('SO:0002');
	});

	// The write path that index addressing breaks: after the first row is removed, the row a
	// curator edits sits at a different position while its dataKey is unchanged.
	it('writes internal to the row it was changed on after another row is deleted', async () => {
		const { editorCallback } = await openDialog();

		fireEvent.click(bodyRows()[0].querySelector('button'));
		const dropdown = cellByHeader(0, 'Internal').querySelector('.p-dropdown');
		fireEvent.click(dropdown);
		fireEvent.click(
			[...document.querySelectorAll('.p-dropdown-item')].find((option) => option.textContent.trim() === 'true')
		);
		keepEdits();

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const saved = editorCallback.mock.calls.at(-1)[0];
		expect(saved).toHaveLength(1);
		expect(saved[0]).toMatchObject({ internal: true, mutationTypes: [{ curie: 'SO:0002' }] });
	});

	it('writes internal to the second row without touching the first', async () => {
		const { editorCallback } = await openDialog();

		const dropdown = cellByHeader(1, 'Internal').querySelector('.p-dropdown');
		fireEvent.click(dropdown);
		fireEvent.click(
			[...document.querySelectorAll('.p-dropdown-item')].find((option) => option.textContent.trim() === 'true')
		);
		keepEdits();

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const saved = editorCallback.mock.calls.at(-1)[0];
		expect(saved[0].internal).toBe(false);
		expect(saved[1].internal).toBe(true);
	});

	it('keeps the rows and shows the message when a row fails validation', async () => {
		const { editorCallback } = await openDialog();
		validate.mockResolvedValueOnce({
			isSuccess: false,
			isError: true,
			data: { mutationTypes: 'Mutation type is required' },
		});

		keepEdits();

		await waitFor(() => expect(cellByHeader(0, 'Mutation Types').textContent).toContain('Mutation type is required'));
		expect(cellByHeader(1, 'Mutation Types').textContent).not.toContain('Mutation type is required');
		expect(editorCallback).not.toHaveBeenCalled();
	});

	it('hands nothing back when the dialog is cancelled', async () => {
		const { editorCallback, setOriginalMutationTypesData } = await openDialog();

		const dropdown = cellByHeader(0, 'Internal').querySelector('.p-dropdown');
		fireEvent.click(dropdown);
		fireEvent.click(
			[...document.querySelectorAll('.p-dropdown-item')].find((option) => option.textContent.trim() === 'true')
		);
		fireEvent.click(getButton('Cancel'));

		expect(editorCallback).not.toHaveBeenCalled();
		expect(setOriginalMutationTypesData).toHaveBeenCalled();
	});

	// Every row carries a dataKey while the dialog is open; it must not reach the main table.
	it('strips dataKey from the rows it hands back', async () => {
		const { editorCallback } = await openDialog();

		keepEdits();

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		for (const row of editorCallback.mock.calls.at(-1)[0]) {
			expect('dataKey' in row).toBe(false);
		}
	});
});
