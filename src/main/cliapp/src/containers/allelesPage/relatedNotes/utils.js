import { addDataKey, sameRowsBy, sortedCuries } from '../utils';
import { copyRow } from '../../../utils/copyRows';

/**
 * A note's curator-editable fields, to compare by: its text, type, internal flag and references.
 *
 * @param {Object} [note]
 * @returns {Array|null} the fields, or none when there is no note
 */
export const relatedNoteValue = (note) =>
	note
		? [note.freeText ?? null, note.noteType?.name ?? null, Boolean(note.internal), sortedCuries(note.references)]
		: null;

/**
 * Readies an allele's related notes for their table: each gets its own `dataKey`.
 *
 * @param {Object[]} [notes] notes as the API returns them
 * @returns {Object[]} copies ready for the table; the argument is not modified
 */
export const prepareRelatedNotes = (notes) =>
	(notes ?? []).map((note) => {
		const row = { ...note };
		addDataKey(row);
		return row;
	});

/**
 * A note as the related notes sub-resource takes it, without the `dataKey` the table keeps for itself.
 *
 * @param {Object} note
 * @returns {Object} a copy without `dataKey`
 */
export const stripRelatedNote = (note) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, ...rest } = note;
	return rest;
};

/**
 * Whether two lists hold the same notes, row by row: the same note with the same text, type, internal flag and
 * references.
 *
 * @param {Object[]} notes
 * @param {Object[]} otherNotes
 * @returns {boolean}
 */
export const haveSameRelatedNotes = sameRowsBy((note) => JSON.stringify([note.id ?? null, relatedNoteValue(note)]));

/**
 * Copies of an allele's related notes for a new allele. Each copy loses its id and audit fields, so it is created
 * afresh.
 *
 * @param {Object[]} [notes] notes as the API returns them
 * @returns {Object[]} rows ready for the table, each with its own `dataKey`
 */
export const buildRelatedNoteCopies = (notes) => prepareRelatedNotes((notes ?? []).map(copyRow));
