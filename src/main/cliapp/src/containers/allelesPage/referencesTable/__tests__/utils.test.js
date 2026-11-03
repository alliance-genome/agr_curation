import { haveSameReferences, prepareReferences, stripReference } from '../utils';

describe('prepareReferences', () => {
	it('Keys each reference and reads its citation and cross reference curies as the API returns them', () => {
		const stored = { curie: 'AGRKB:1', shortCitation: 'Doe 2020', crossReferences: [{ referencedCurie: 'PMID:1' }] };

		const [row] = prepareReferences([stored]);

		expect(row.dataKey).toBeTruthy();
		expect(row.shortCitation).toBe('Doe 2020');
		expect(row.crossReferencesFilter).toBe('PMID:1');
		expect(stored).not.toHaveProperty('dataKey');
	});

	it('Reads the citation and cross reference curies as the reference search returns them', () => {
		const [row] = prepareReferences([
			{ curie: 'AGRKB:1', short_citation: 'Doe 2020', cross_references: [{ curie: 'PMID:1' }, { curie: 'DOI:1' }] },
		]);

		expect(row.shortCitation).toBe('Doe 2020');
		expect(row.crossReferencesFilter).toBe('PMID:1,DOI:1');
	});

	it('Readies no rows from no references', () => {
		expect(prepareReferences(undefined)).toEqual([]);
	});
});

describe('stripReference', () => {
	it('Keeps only the curie, with the type the API reads it by', () => {
		expect(stripReference({ curie: 'AGRKB:1', dataKey: 'k', shortCitation: 'Doe 2020', id: 7 })).toEqual({
			type: 'Reference',
			curie: 'AGRKB:1',
		});
	});
});

describe('haveSameReferences', () => {
	const first = { curie: 'AGRKB:1', dataKey: 'a' };
	const second = { curie: 'AGRKB:2', dataKey: 'b' };

	it('Matches the same references in any order, whatever their row keys', () => {
		expect(haveSameReferences([first, second], [{ ...second, dataKey: 'c' }, first])).toBe(true);
	});

	it('Tells apart lists holding different references', () => {
		expect(haveSameReferences([first], [second])).toBe(false);
		expect(haveSameReferences([first], [first, second])).toBe(false);
	});
});
