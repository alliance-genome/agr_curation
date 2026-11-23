import {
	buildCassetteCreatePayload,
	buildCassettePayload,
	buildDuplicateCassette,
	copyComponentRows,
	haveSameComponentRows,
} from '../detail/utils';

describe('buildCassettePayload', () => {
	const formCassette = {
		id: 11,
		cassetteSymbol: { dataKey: 'k1', displayText: 'UAS-GFP' },
		cassetteFullName: null,
		cassetteSynonyms: [{ dataKey: 'k2', displayText: 'UAS-EGFP' }],
		cassetteUses: [{ dataKey: 'k3', uses: [{ curie: 'FBcv:0005012' }] }],
		relatedNotes: [{ id: 3, freeText: 'note' }],
		references: [{ dataKey: 'k5', curie: 'AGRKB:101', shortCitation: 'x', crossReferencesFilter: 'PMID:1' }],
		cassetteComponents: [{ id: 41 }],
		cassetteGenomicEntityAssociations: [{ id: 42 }],
		cassetteTransgenicToolAssociations: [{ id: 43 }],
		cassetteStrAssociations: [{ id: 44 }],
	};

	it('drops the keys the tables add and the component lists, and keeps the related notes as read', () => {
		const payload = buildCassettePayload(formCassette);

		expect(payload.cassetteSymbol).toEqual({ displayText: 'UAS-GFP' });
		expect(payload.cassetteFullName).toBeNull();
		expect(payload.cassetteSynonyms).toEqual([{ displayText: 'UAS-EGFP' }]);
		expect(payload.cassetteUses).toEqual([{ uses: [{ curie: 'FBcv:0005012' }] }]);
		expect(payload.references).toEqual([{ type: 'Reference', curie: 'AGRKB:101' }]);
		expect(payload.relatedNotes).toEqual([{ id: 3, freeText: 'note' }]);
		[
			'cassetteComponents',
			'cassetteGenomicEntityAssociations',
			'cassetteTransgenicToolAssociations',
			'cassetteStrAssociations',
		].forEach((field) => expect(payload).not.toHaveProperty(field));
	});

	it('leaves the form cassette unchanged', () => {
		const before = structuredClone(formCassette);
		buildCassettePayload(formCassette);
		expect(formCassette).toEqual(before);
	});
});

describe('haveSameComponentRows', () => {
	const row = { id: 41, componentSymbol: 'GFP', relation: { name: 'tagged_with' } };

	it('ignores the keys the table adds and the cassette each row belongs to', () => {
		expect(
			haveSameComponentRows([{ ...row, dataKey: 'a', singleCassette: { id: 11 } }], [{ ...row, dataKey: 'b' }])
		).toBe(true);
	});

	it('tells an edited, added or removed row apart', () => {
		expect(haveSameComponentRows([row], [{ ...row, componentSymbol: 'RFP' }])).toBe(false);
		expect(haveSameComponentRows([row], [row, { componentSymbol: 'RFP' }])).toBe(false);
		expect(haveSameComponentRows([row], [])).toBe(false);
	});
});

describe('buildCassetteCreatePayload', () => {
	it('builds the payload as for an update, with the type the API reads it by', () => {
		const payload = buildCassetteCreatePayload({
			cassetteSymbol: { dataKey: 'k1', displayText: 'UAS-GFP' },
			cassetteComponents: [{ componentSymbol: 'GFP' }],
			cassetteGenomicEntityAssociations: [{ relation: { name: 'targets' } }],
		});

		expect(payload.type).toBe('Cassette');
		expect(payload.cassetteSymbol).toEqual({ displayText: 'UAS-GFP' });
		expect(payload.cassetteComponents).toEqual([]);
		expect(payload).not.toHaveProperty('cassetteGenomicEntityAssociations');
	});

	it('carries the free text component rows, as the replace endpoint takes them', () => {
		const rows = [
			{ dataKey: 'k2', componentSymbol: 'GFP', singleCassette: { id: 11 }, relation: { name: 'tagged_with' } },
		];

		const payload = buildCassetteCreatePayload({ cassetteSymbol: { displayText: 'UAS-GFP' } }, rows);

		expect(payload.cassetteComponents).toEqual([{ componentSymbol: 'GFP', relation: { name: 'tagged_with' } }]);
		expect(rows[0].dataKey).toBe('k2');
	});
});

describe('buildDuplicateCassette', () => {
	const stored = {
		id: 11,
		curie: 'AGRKB:123000000000001',
		uniqueId: 'FB:FBcs0000001',
		primaryExternalId: 'FB:FBcs0000001',
		modInternalId: 'cs1',
		secondaryIdentifiers: ['FB:FBcs9000001'],
		dataProvider: { abbreviation: 'FB' },
		createdBy: { uniqueId: 'curator' },
		dateCreated: '2026-01-01',
		relatedNotes: [{ id: 3, freeText: 'note' }],
		cassetteSymbol: { id: 5, displayText: 'UAS-GFP', createdBy: { uniqueId: 'curator' } },
		cassetteFullName: null,
		cassetteSynonyms: [{ id: 1, displayText: 'UAS-EGFP' }],
		cassetteUses: [{ id: 2, uses: [{ id: 70, curie: 'FBcv:0005012' }] }],
		references: [{ id: 80, curie: 'AGRKB:101' }],
		cassetteComponents: [{ id: 41 }],
		placeholder: true,
		internal: true,
		obsolete: true,
	};

	it('keeps the fields the create form edits, each row without its id or audit fields', () => {
		expect(buildDuplicateCassette(stored)).toEqual({
			type: 'Cassette',
			placeholder: true,
			internal: true,
			obsolete: false,
			cassetteSymbol: { displayText: 'UAS-GFP' },
			cassetteFullName: null,
			cassetteSynonyms: [{ displayText: 'UAS-EGFP' }],
			secondaryIdentifiers: ['FB:FBcs9000001'],
			cassetteUses: [{ uses: [{ id: 70, curie: 'FBcv:0005012' }] }],
			references: [{ id: 80, curie: 'AGRKB:101' }],
		});
	});

	it('leaves the stored cassette unchanged', () => {
		const before = structuredClone(stored);
		buildDuplicateCassette(stored);
		expect(stored).toEqual(before);
	});
});

describe('copyComponentRows', () => {
	it("copies each row and its notes as new ones, without the cassette, ready for the list's table", () => {
		const rows = [
			{
				id: 41,
				dataKey: 'a',
				singleCassette: { id: 11 },
				componentSymbol: 'GFP',
				relatedNotes: [{ id: 61, freeText: 'From jellyfish.', dateCreated: '2026-01-01' }],
			},
			{ id: 42, dataKey: 'b', cassetteAssociationSubject: { id: 11 }, evidence: [{ id: 80, curie: 'AGRKB:101' }] },
		];

		const copies = copyComponentRows(rows);

		expect(copies.map(({ dataKey, ...row }) => row)).toEqual([
			{ componentSymbol: 'GFP', relatedNotes: [{ freeText: 'From jellyfish.' }] },
			{ evidence: [{ id: 80, curie: 'AGRKB:101' }], relatedNotes: [] },
		]);
		copies.forEach((copy) => expect(copy.dataKey).toEqual(expect.any(String)));
		expect(copies[0].dataKey).not.toBe('a');
		expect(rows[0].id).toBe(41);
	});
});
