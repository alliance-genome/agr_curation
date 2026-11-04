import { buildCassettePayload, haveSameComponentRows } from '../detail/utils';

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
