import { markPendingEdits } from '../pendingEdits';

const PENDING = { severity: 'warn', message: 'Pending Edits!' };

describe('markPendingEdits', () => {
	it('adds the warning to a table with no errors', () => {
		expect(markPendingEdits({}, 2, 'relatedNotes')).toEqual({ 2: { relatedNotes: PENDING } });
	});

	it('adds the warning when the table has no errors object yet', () => {
		expect(markPendingEdits(undefined, 'row-7', 'relatedNote')).toEqual({ 'row-7': { relatedNote: PENDING } });
	});

	it("keeps the row's other warnings and errors", () => {
		const symbolError = { severity: 'error', message: 'symbol is bad' };

		expect(markPendingEdits({ 2: { synonyms: PENDING, symbol: symbolError } }, 2, 'relatedNotes')).toEqual({
			2: { synonyms: PENDING, symbol: symbolError, relatedNotes: PENDING },
		});
	});

	it('replaces an earlier error on the same field', () => {
		const notesError = { severity: 'error', message: 'notes are bad' };

		expect(markPendingEdits({ 2: { relatedNotes: notesError } }, 2, 'relatedNotes')).toEqual({
			2: { relatedNotes: PENDING },
		});
	});

	it('leaves the other rows alone', () => {
		const otherRow = { synonyms: PENDING };

		expect(markPendingEdits({ 1: otherRow }, 2, 'relatedNotes')).toEqual({
			1: otherRow,
			2: { relatedNotes: PENDING },
		});
	});

	it('does not change the errors it is given', () => {
		const errorMessages = { 2: { synonyms: PENDING } };

		markPendingEdits(errorMessages, 2, 'relatedNotes');

		expect(errorMessages).toEqual({ 2: { synonyms: PENDING } });
	});
});
