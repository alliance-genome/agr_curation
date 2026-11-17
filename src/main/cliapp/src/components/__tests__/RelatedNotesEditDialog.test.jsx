import { fireEvent, render, waitFor } from '@testing-library/react';
import { RelatedNotesEditDialog } from '../RelatedNotesEditDialog';
import { termSetTermsFor } from '../../service/__mocks__/useVocabularyTermSetService';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	selectInColumn,
	typeInColumn,
} from '../../containers/allelesPage/__tests__/alleleFormTestUtils';
import { SearchService } from '../../service/SearchService';

vi.mock('../../service/SearchService');
vi.mock('../../service/useControlledVocabularyService');
vi.mock('../../service/useVocabularyTermSetService');

// The pop-up constructs a ValidationService at render and calls it once per note on Keep Edits.
const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

const NOTE_TYPE_SET = 'gene_note_type';
const REFERENCE = { id: 1, curie: 'AGRKB:101' };
const NEW_REFERENCE = { id: 3, curie: 'AGRKB:103' };
const VALID = { isSuccess: true, isError: false, data: {} };
const COLUMN_ERRORS = [
	['Note Type', 'noteType', 'note type is bad'],
	['Text', 'freeText', 'text is bad'],
	['Evidence', 'references', 'references are bad'],
	['Internal', 'internal', 'internal is bad'],
];

const note = (freeText, overrides = {}) => ({
	noteType: termSetTermsFor(NOTE_TYPE_SET)[0],
	freeText,
	internal: false,
	references: [REFERENCE],
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

const textIn = (container, header, rowIndex = 0) =>
	getCellByHeader(container, header, rowIndex).querySelector('textarea').value;

const columnHeaders = (container) =>
	[...container.querySelectorAll('thead tr:first-child th')].map((header) => header.textContent.trim());

const bodyRows = (baseElement) =>
	[...baseElement.querySelectorAll('.p-dialog tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

/** Picks a reference suggestion and waits for its panel to finish closing, which it does after the pick. */
const pickReference = async (container, rowIndex = 0) => {
	await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/, rowIndex);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

/**
 * The related notes pop-up the entity tables open. It edits a row's notes in local state and hands them back to
 * the main row on Keep Edits, once every note passes validation.
 */
describe('<RelatedNotesEditDialog />', () => {
	const openDialog = async (originalRelatedNotes = [note('first note')], props = {}) => {
		const editorCallback = vi.fn();
		const setErrorMessagesMainRow = vi.fn();
		const result = render(
			<RelatedNotesEditDialog
				originalRelatedNotesData={{
					originalRelatedNotes,
					isInEdit: true,
					dialog: true,
					rowIndex: 2,
					mainRowProps: { editorCallback },
				}}
				setOriginalRelatedNotesData={vi.fn()}
				errorMessagesMainRow={{}}
				setErrorMessagesMainRow={setErrorMessagesMainRow}
				noteTypeVocabularyTermSet={NOTE_TYPE_SET}
				{...props}
			/>
		);
		// The pop-up copies its notes in onShow, which runs as the open transition ends.
		await waitFor(() => {
			expect(result.baseElement.querySelector('.p-dialog-enter-done')).not.toBeNull();
			expect(bodyRows(result.baseElement)).toHaveLength(originalRelatedNotes.length);
		});
		return { ...result, editorCallback, setErrorMessagesMainRow };
	};

	const keepEdits = (baseElement) => fireEvent.click(getButton(baseElement, 'Keep Edits'));

	beforeEach(() => {
		SearchService.prototype.search = vi.fn(() => Promise.resolve({ results: [NEW_REFERENCE] }));
		validate.mockReset();
		validate.mockResolvedValue(VALID);
	});

	it("opens with the row's notes", async () => {
		const { baseElement } = await openDialog([note('first note'), note('second note', { internal: true })]);

		expect(baseElement.querySelector('.p-dialog h3').textContent).toBe('Related Notes');
		expect(dropdownLabel(baseElement, 'Note Type')).toBe(`${NOTE_TYPE_SET}_alpha`);
		expect(textIn(baseElement, 'Text')).toBe('first note');
		expect(getCellByHeader(baseElement, 'Evidence').textContent).toContain(REFERENCE.curie);
		expect(dropdownLabel(baseElement, 'Internal')).toBe('false');
		expect(textIn(baseElement, 'Text', 1)).toBe('second note');
		expect(dropdownLabel(baseElement, 'Internal', 1)).toBe('true');
	});

	it('offers the note types of the term set it is given', async () => {
		const { baseElement } = await openDialog();

		fireEvent.click(getCellByHeader(baseElement, 'Note Type').querySelector('.p-dropdown'));

		const options = [...document.querySelectorAll('.p-dropdown-item')].map((item) => item.textContent.trim());
		expect(options).toEqual([`${NOTE_TYPE_SET}_alpha`, `${NOTE_TYPE_SET}_beta`]);
	});

	it('hands back the edited notes, each validated as a note, and marks the row as having pending edits', async () => {
		const { baseElement, editorCallback, setErrorMessagesMainRow } = await openDialog();

		selectInColumn(baseElement, 'Note Type', `${NOTE_TYPE_SET}_beta`);
		typeInColumn(baseElement, 'Text', 'edited note');
		await pickReference(baseElement);
		selectInColumn(baseElement, 'Internal', 'true');
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{
				noteType: termSetTermsFor(NOTE_TYPE_SET)[1],
				freeText: 'edited note',
				internal: true,
				references: [REFERENCE, NEW_REFERENCE],
			},
		]);
		expect(validate).toHaveBeenCalledTimes(1);
		expect(validate).toHaveBeenCalledWith('note', {
			noteType: termSetTermsFor(NOTE_TYPE_SET)[1],
			freeText: 'edited note',
			internal: true,
			references: [REFERENCE, NEW_REFERENCE],
		});
		expect(setErrorMessagesMainRow).toHaveBeenCalledWith({
			2: { relatedNotes: { severity: 'warn', message: 'Pending Edits!' } },
		});
	});

	it('hands back a new note added with New Note', async () => {
		const { baseElement, editorCallback } = await openDialog();

		fireEvent.click(getButton(baseElement, 'New Note'));
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(2));
		selectInColumn(baseElement, 'Note Type', `${NOTE_TYPE_SET}_beta`, 1);
		typeInColumn(baseElement, 'Text', 'new note', 1);
		await pickReference(baseElement, 1);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		const handedBack = editorCallback.mock.calls[0][0];
		expect(handedBack).toHaveLength(2);
		expect(handedBack[0]).toEqual(note('first note'));
		expect(handedBack[1]).toMatchObject({
			noteType: termSetTermsFor(NOTE_TYPE_SET)[1],
			freeText: 'new note',
			internal: false,
			references: [NEW_REFERENCE],
		});
	});

	it('saves each edit on the note that moved up after the note above it is deleted', async () => {
		const { baseElement, editorCallback } = await openDialog([note('first note'), note('second note')]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		selectInColumn(baseElement, 'Note Type', `${NOTE_TYPE_SET}_beta`);
		typeInColumn(baseElement, 'Text', 'second note edited');
		selectInColumn(baseElement, 'Internal', 'true');
		await pickReference(baseElement);
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback).toHaveBeenCalledWith([
			{
				noteType: termSetTermsFor(NOTE_TYPE_SET)[1],
				freeText: 'second note edited',
				internal: true,
				references: [REFERENCE, NEW_REFERENCE],
			},
		]);
	});

	it('shows each validation error on its own cell of the note that caused it', async () => {
		validate.mockResolvedValueOnce(VALID).mockResolvedValueOnce({
			isSuccess: false,
			isError: true,
			data: Object.fromEntries(COLUMN_ERRORS.map(([, field, message]) => [field, message])),
		});
		const { baseElement } = await openDialog([note('first note'), note('second note')]);

		keepEdits(baseElement);

		await waitFor(() => expect(getCellByHeader(baseElement, 'Text', 1).textContent).toContain('text is bad'));
		COLUMN_ERRORS.forEach(([header, , message]) => {
			expect(getCellByHeader(baseElement, header, 1).textContent).toContain(message);
			expect(getCellByHeader(baseElement, header, 0).textContent).not.toContain(message);
			COLUMN_ERRORS.filter(([otherHeader]) => otherHeader !== header).forEach(([, , otherMessage]) =>
				expect(getCellByHeader(baseElement, header, 1).textContent).not.toContain(otherMessage)
			);
		});
	});

	it('shows a validation error on the note that caused it and keeps the edits back', async () => {
		validate
			.mockResolvedValueOnce(VALID)
			.mockResolvedValueOnce({ isSuccess: false, isError: true, data: { freeText: 'second note is bad' } });
		const { baseElement, editorCallback } = await openDialog([note('first note'), note('second note')]);

		keepEdits(baseElement);

		await waitFor(() => expect(getCellByHeader(baseElement, 'Text', 1).textContent).toContain('second note is bad'));
		expect(getCellByHeader(baseElement, 'Text', 0).textContent).not.toContain('second note is bad');
		expect(editorCallback).not.toHaveBeenCalled();
	});

	it('shows a validation error on the right note after the note above it is deleted', async () => {
		validate.mockResolvedValue({ isSuccess: false, isError: true, data: { freeText: 'remaining note is bad' } });
		const { baseElement } = await openDialog([note('first note'), note('second note')]);

		clickDeleteInRow(baseElement, 0);
		await waitFor(() => expect(bodyRows(baseElement)).toHaveLength(1));
		keepEdits(baseElement);

		await waitFor(() => expect(getCellByHeader(baseElement, 'Text', 0).textContent).toContain('remaining note is bad'));
		expect(textIn(baseElement, 'Text')).toBe('second note');
	});

	it('leaves out the evidence column when told to', async () => {
		const { baseElement, editorCallback } = await openDialog([note('first note', { references: undefined })], {
			showReferences: false,
		});

		expect(columnHeaders(baseElement)).toEqual(['Actions', 'Note Type', 'Text', 'Internal']);

		typeInColumn(baseElement, 'Text', 'edited note');
		keepEdits(baseElement);

		await waitFor(() => expect(editorCallback).toHaveBeenCalled());
		expect(editorCallback.mock.calls[0][0][0]).toMatchObject({ freeText: 'edited note' });
	});
});
