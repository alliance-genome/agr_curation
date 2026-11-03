import { render, fireEvent, waitFor } from '@testing-library/react';
import { SynonymsEditDialog } from '../SynonymsEditDialog';
import { vocabularyTermsFor } from '../../../../service/__mocks__/useControlledVocabularyService';
import {
	getCellByHeader,
	selectInColumn,
	typeInColumn,
	clickDeleteInRow,
} from '../../../allelesPage/__tests__/alleleFormTestUtils';

vi.mock('../../../../service/useControlledVocabularyService');

// The dialog constructs a ValidationService at render and calls it once per row on
// "Keep Edits"; the save path cannot be exercised without standing in for it.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const makeRow = (displayText) => ({
	displayText,
	formatText: displayText,
	synonymScope: null,
	nameType: null,
	synonymUrl: '',
	internal: false,
	evidence: [],
});

const ROWS = [makeRow('syn A'), makeRow('syn B')];

const renderDialog = async (
	originalSynonyms = ROWS,
	{ field = 'alleleSynonyms', endpoint = 'allelesynonymslotannotation' } = {}
) => {
	const editorCallback = vi.fn();
	const setErrorMessagesMainRow = vi.fn();
	const result = render(
		<SynonymsEditDialog
			field={field}
			endpoint={endpoint}
			originalSynonymsData={{
				originalSynonyms,
				isInEdit: true,
				dialog: true,
				rowIndex: 0,
				mainRowProps: { editorCallback },
			}}
			setOriginalSynonymsData={vi.fn()}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={setErrorMessagesMainRow}
		/>
	);

	// The dialog clones its rows and assigns their dataKeys in onShow, which PrimeReact
	// fires at the end of the open transition. Waiting on a populated first row rather
	// than on the row count: before onShow the table renders a single empty-message row,
	// which a bare count check mistakes for one real row.
	await waitFor(() => {
		const rows = result.baseElement.querySelectorAll('tbody tr');
		expect(rows).toHaveLength(originalSynonyms.length);
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

/**
 * The edit dialog owns its rows in local state and hands all of them back to the main row,
 * since an entity holds a list of synonyms.
 */
describe('<SynonymsEditDialog />', () => {
	beforeEach(() => {
		validate.mockReset();
		validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	});

	it('opens with one editable row per synonym', async () => {
		const { baseElement } = await renderDialog();

		expect(getCellByHeader(baseElement, 'Display Text', 0).querySelector('input').value).toBe('syn A');
		expect(getCellByHeader(baseElement, 'Display Text', 1).querySelector('input').value).toBe('syn B');
	});

	// The header group and the body columns are declared separately, so nothing else
	// checks they agree — and a dropped column silently shifts every column after it.
	it('renders one body cell per declared header', async () => {
		const { baseElement } = await renderDialog();

		const headers = baseElement.querySelectorAll('thead tr:first-child th');
		const cells = baseElement.querySelectorAll('tbody tr')[0].querySelectorAll('td');

		expect(cells).toHaveLength(headers.length);
	});

	// Synonym name types come from the name_type vocabulary, where symbol and full name use a
	// vocabulary term set. Each mocked vocabulary names its terms after its label, so drawing
	// from the wrong one leaves this option missing.
	it('draws nameType from the name_type vocabulary', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		selectInColumn(baseElement, 'Name Type', 'name_type_alpha', 0);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ ...makeRow('syn A'), nameType: vocabularyTermsFor('name_type')[0] },
			makeRow('syn B'),
		]);
	});

	it('saves the whole synonym scope term', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		selectInColumn(baseElement, 'Synonym Scope', 'synonym_scope_beta', 0);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ ...makeRow('syn A'), synonymScope: vocabularyTermsFor('synonym_scope')[1] },
			makeRow('syn B'),
		]);
	});

	// The Alleles, Genes and Affected Genomic Models tables share this dialog, each with its
	// own field and endpoint.
	it('validates against its endpoint and files the pending-edit warning under its field', async () => {
		const { baseElement, setErrorMessagesMainRow } = await renderDialog(ROWS, {
			field: 'geneSynonyms',
			endpoint: 'genesynonymslotannotation',
		});

		keepEdits(baseElement);

		await waitFor(() => expect(setErrorMessagesMainRow).toHaveBeenCalled());
		expect(validate).toHaveBeenCalledWith('genesynonymslotannotation', expect.anything());
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			0: { geneSynonyms: { severity: 'warn', message: 'Pending Edits!' } },
		});
	});

	it('hands every row back to the main row editorCallback, with their dataKeys stripped', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		typeInColumn(baseElement, 'Display Text', 'syn EDITED', 0);
		selectInColumn(baseElement, 'Internal', 'true', 0);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{ ...makeRow('syn A'), displayText: 'syn EDITED', internal: true },
			makeRow('syn B'),
		]);
	});

	// A second row exists in the dialog even though only the first is kept, so an edit
	// misdirected by one position would be silently discarded rather than misfiled.
	it('edits the row the curator clicked, leaving its sibling alone', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		typeInColumn(baseElement, 'Display Text', 'syn B EDITED', 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			makeRow('syn A'),
			{ ...makeRow('syn B'), displayText: 'syn B EDITED' },
		]);
	});

	it('adds a new synonym with the first name type selected', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		fireEvent.click(getButton(baseElement, 'New Synonym'));
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const saved = editorCallback.mock.calls.at(-1)[0];
		expect(saved).toHaveLength(3);
		expect(saved[2]).toMatchObject({ internal: false, nameType: vocabularyTermsFor('name_type')[0] });
	});

	// Deleting a row moves the rows after it to new positions while their dataKeys stay the
	// same, so an edit addressed by position lands on a different row.
	it('drops the deleted row and edits the survivor the curator clicked', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		clickDeleteInRow(baseElement, 0);

		await waitFor(() => expect(baseElement.querySelectorAll('tbody tr')).toHaveLength(1));

		selectInColumn(baseElement, 'Internal', 'true', 0);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([{ ...makeRow('syn B'), internal: true }]);
	});

	it('renders a validation error against the row it was keyed to, and does not save', async () => {
		const { baseElement, editorCallback } = await renderDialog();

		validate.mockImplementation((endpoint, synonym) =>
			Promise.resolve(
				synonym.displayText === 'syn B'
					? { isSuccess: false, isError: true, data: { displayText: 'must be unique' } }
					: { isSuccess: true, isError: false, data: {} }
			)
		);

		keepEdits(baseElement);

		await waitFor(() =>
			expect(getCellByHeader(baseElement, 'Display Text', 1).textContent).toContain('must be unique')
		);
		expect(getCellByHeader(baseElement, 'Display Text', 0).textContent).not.toContain('must be unique');
		expect(editorCallback).not.toHaveBeenCalled();
	});
});
