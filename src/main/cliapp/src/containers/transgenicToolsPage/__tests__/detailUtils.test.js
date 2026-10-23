import {
	buildTransgenicToolPayload,
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
