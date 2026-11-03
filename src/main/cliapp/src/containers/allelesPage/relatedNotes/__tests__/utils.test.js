import { buildRelatedNoteCopies, haveSameRelatedNotes, stripRelatedNote } from '../utils';

describe('haveSameRelatedNotes', () => {
	const stored = [
		{
			id: 1,
			freeText: 'a note',
			noteType: { id: 4, name: 'comment' },
			internal: false,
			references: [{ curie: 'AGRKB:1' }, { curie: 'AGRKB:2' }],
		},
	];

	it('Reads the same note with its references in another order as unchanged', () => {
		expect(
			haveSameRelatedNotes(
				[{ ...stored[0], noteType: { name: 'comment' }, references: [{ curie: 'AGRKB:2' }, { curie: 'AGRKB:1' }] }],
				stored
			)
		).toBe(true);
	});

	it('Finds an edited text, type or internal flag', () => {
		expect(haveSameRelatedNotes([{ ...stored[0], freeText: 'edited' }], stored)).toBe(false);
		expect(haveSameRelatedNotes([{ ...stored[0], noteType: { name: 'user_submitted_note' } }], stored)).toBe(false);
		expect(haveSameRelatedNotes([{ ...stored[0], internal: true }], stored)).toBe(false);
	});

	it('Reads a new note with the same text as a different note', () => {
		expect(haveSameRelatedNotes([{ ...stored[0], id: undefined }], stored)).toBe(false);
	});
});

describe('stripRelatedNote', () => {
	it('Drops only the dataKey', () => {
		expect(stripRelatedNote({ id: 1, dataKey: 'k', freeText: 'a note' })).toEqual({ id: 1, freeText: 'a note' });
	});
});

describe('buildRelatedNoteCopies', () => {
	it('Copies each note without its id or audit fields', () => {
		const [copy] = buildRelatedNoteCopies([
			{ id: 1, dateCreated: '2024-01-01', updatedBy: { id: 2 }, freeText: 'a note', noteType: { name: 'comment' } },
		]);

		expect(copy).not.toHaveProperty('id');
		expect(copy).not.toHaveProperty('dateCreated');
		expect(copy).not.toHaveProperty('updatedBy');
		expect(copy).toMatchObject({ freeText: 'a note', noteType: { name: 'comment' } });
		expect(copy.dataKey).toBeDefined();
	});
});
