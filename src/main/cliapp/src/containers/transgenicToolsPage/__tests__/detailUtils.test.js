import {
	buildDuplicateTransgenicTool,
	buildTransgenicToolCreatePayload,
	buildTransgenicToolPayload,
	copyToolAssociationRows,
	haveSameToolAssociations,
	prepareToolAssociations,
	stripToolAssociation,
} from '../detail/utils';

describe('buildTransgenicToolPayload', () => {
	const formTool = {
		id: 11,
		transgenicToolSymbol: { dataKey: 'k1', displayText: 'GAL4' },
		transgenicToolFullName: null,
		transgenicToolSynonyms: [{ dataKey: 'k2', displayText: 'Gal4p' }],
		transgenicToolUses: [{ dataKey: 'k3', uses: [{ curie: 'FBcv:0005012' }] }],
		relatedNotes: [{ dataKey: 'k4', freeText: 'note' }],
		references: [{ dataKey: 'k5', curie: 'AGRKB:101', shortCitation: 'x', crossReferencesFilter: 'PMID:1' }],
		crossReferences: [{ dataKey: 'k6', referencedCurie: 'FB:FBto1', resourceDescriptor: { prefix: 'FB' } }],
		associatedTransgenicTools: [{ relation: 'is_compatible_with', symbol: 'UAS' }],
	};

	it('drops the keys and helper fields the tables add, and the computed associations', () => {
		const payload = buildTransgenicToolPayload(formTool);

		expect(payload.transgenicToolSymbol).toEqual({ displayText: 'GAL4' });
		expect(payload.transgenicToolFullName).toBeNull();
		expect(payload.transgenicToolSynonyms).toEqual([{ displayText: 'Gal4p' }]);
		expect(payload.transgenicToolUses).toEqual([{ uses: [{ curie: 'FBcv:0005012' }] }]);
		expect(payload.relatedNotes).toEqual([{ freeText: 'note' }]);
		expect(payload.references).toEqual([{ type: 'Reference', curie: 'AGRKB:101' }]);
		expect(payload.crossReferences).toEqual([{ referencedCurie: 'FB:FBto1' }]);
		expect(payload).not.toHaveProperty('associatedTransgenicTools');
	});

	it('leaves the form tool unchanged', () => {
		const before = structuredClone(formTool);
		buildTransgenicToolPayload(formTool);
		expect(formTool).toEqual(before);
	});
});

describe('stripToolAssociation', () => {
	it('sends the other tool by id and leaves out the subject and the row key', () => {
		const row = {
			dataKey: 'k1',
			id: 21,
			relation: { name: 'is_compatible_with' },
			transgenicToolAssociationSubject: { id: 11 },
			transgenicToolTransgenicToolAssociationObject: { id: 12, primaryExternalId: 'FB:FBto0000135' },
			internal: true,
		};

		expect(stripToolAssociation(row)).toEqual({
			id: 21,
			relation: { name: 'is_compatible_with' },
			transgenicToolTransgenicToolAssociationObject: { type: 'TransgenicTool', id: 12 },
			internal: true,
		});
	});

	it('sends a tool typed rather than picked as no tool', () => {
		const row = { transgenicToolTransgenicToolAssociationObject: { primaryExternalId: 'typed' } };
		expect(stripToolAssociation(row).transgenicToolTransgenicToolAssociationObject).toBeNull();
	});
});

describe('haveSameToolAssociations', () => {
	const association = {
		id: 21,
		relation: { name: 'is_compatible_with' },
		transgenicToolTransgenicToolAssociationObject: { id: 12 },
	};

	it('matches the same rows in any order, ignoring row keys', () => {
		const other = { ...association, id: 22, transgenicToolTransgenicToolAssociationObject: { id: 13 } };
		expect(haveSameToolAssociations(prepareToolAssociations([association, other]), [other, association])).toBe(true);
	});

	it('tells an edited row apart', () => {
		expect(haveSameToolAssociations([{ ...association, internal: true }], [association])).toBe(false);
		expect(haveSameToolAssociations([], [association])).toBe(false);
	});
});

describe('buildTransgenicToolCreatePayload', () => {
	it('is the tool as the update takes it, with its type and without the associations summary', () => {
		const payload = buildTransgenicToolCreatePayload({
			transgenicToolSymbol: { dataKey: 'k1', displayText: 'GAL4' },
			placeholder: false,
			associatedTransgenicTools: [{ symbol: 'UAS' }],
		});

		expect(payload.type).toBe('TransgenicTool');
		expect(payload.transgenicToolSymbol).toEqual({ displayText: 'GAL4' });
		expect(payload).not.toHaveProperty('id');
		expect(payload).not.toHaveProperty('dataProvider');
		expect(payload).not.toHaveProperty('associatedTransgenicTools');
	});
});

describe('buildDuplicateTransgenicTool', () => {
	const audit = { createdBy: { uniqueId: 'u' }, dateCreated: '2026-01-01', updatedBy: { uniqueId: 'u' } };
	const stored = {
		type: 'TransgenicTool',
		id: 99,
		curie: 'AGRKB:122000000000099',
		uniqueId: 'GAL4|GAL4 transcription factor',
		primaryExternalId: 'FB:FBto0000393',
		modInternalId: 'FB:1',
		dataProvider: { abbreviation: 'FB' },
		...audit,
		transgenicToolSymbol: { id: 5, displayText: 'GAL4', formatText: 'GAL4', ...audit },
		transgenicToolFullName: { id: 6, displayText: 'GAL4 transcription factor', nameType: { id: 7 } },
		transgenicToolSynonyms: [{ id: 1, displayText: 'Gal4p' }],
		secondaryIdentifiers: ['FB:FBto9000393'],
		transgenicToolUses: [{ id: 2, uses: [{ id: 70, curie: 'FBcv:0005012' }] }],
		references: [{ id: 80, curie: 'AGRKB:101000000001' }],
		relatedNotes: [{ id: 3, freeText: 'A note.', noteType: { name: 'comment' } }],
		crossReferences: [{ id: 4, referencedCurie: 'FB:FBto0000393', resourceDescriptorPage: { id: 8 } }],
		associatedTransgenicTools: [{ symbol: 'UAS' }],
		placeholder: true,
		internal: true,
		obsolete: true,
	};

	it("carries the fields the create form edits, without the stored tool's ids, audit fields and rows' ids", () => {
		const copy = buildDuplicateTransgenicTool(stored);

		expect(copy).toEqual({
			type: 'TransgenicTool',
			placeholder: true,
			internal: true,
			obsolete: false,
			transgenicToolSymbol: { displayText: 'GAL4', formatText: 'GAL4' },
			transgenicToolFullName: { displayText: 'GAL4 transcription factor', nameType: { id: 7 } },
			transgenicToolSynonyms: [{ displayText: 'Gal4p' }],
			secondaryIdentifiers: ['FB:FBto9000393'],
			transgenicToolUses: [{ uses: [{ id: 70, curie: 'FBcv:0005012' }] }],
			references: [{ id: 80, curie: 'AGRKB:101000000001' }],
			relatedNotes: [{ freeText: 'A note.', noteType: { name: 'comment' } }],
			crossReferences: [{ referencedCurie: 'FB:FBto0000393', resourceDescriptorPage: { id: 8 } }],
		});
	});

	it('leaves the stored tool unchanged', () => {
		const before = structuredClone(stored);
		buildDuplicateTransgenicTool(stored);
		expect(stored).toEqual(before);
	});
});

describe('copyToolAssociationRows', () => {
	it('copies each row as a new one, without its id, subject and key, keeping the relation and other tool', () => {
		const rows = [
			{
				dataKey: 3,
				id: 21,
				relation: { id: 31, name: 'is_compatible_with' },
				transgenicToolAssociationSubject: { id: 99 },
				transgenicToolTransgenicToolAssociationObject: { id: 12 },
				createdBy: { uniqueId: 'u' },
				internal: false,
				obsolete: false,
			},
		];

		const [copy] = copyToolAssociationRows(rows);

		expect(copy).toMatchObject({
			relation: { id: 31, name: 'is_compatible_with' },
			transgenicToolTransgenicToolAssociationObject: { id: 12 },
			internal: false,
			obsolete: false,
		});
		['id', 'transgenicToolAssociationSubject', 'createdBy'].forEach((field) => expect(copy).not.toHaveProperty(field));
		// a fresh key for the table
		expect(copy.dataKey).toBeDefined();
		expect(rows[0].id).toBe(21);
	});
});
