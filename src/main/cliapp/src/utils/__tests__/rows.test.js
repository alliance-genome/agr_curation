import { sameRowsBy, sortedCuries } from '../rows';

describe('sortedCuries', () => {
	it('Sorts the curies so lists compare whatever their order', () => {
		expect(sortedCuries([{ curie: 'AGRKB:2' }, { curie: 'AGRKB:1' }])).toEqual(['AGRKB:1', 'AGRKB:2']);
	});

	it('Gives none when there is no list', () => {
		expect(sortedCuries(undefined)).toEqual([]);
	});
});

describe('sameRowsBy', () => {
	const sameNames = sameRowsBy((row) => row.name);

	it('Compares the lists row by row by the given value', () => {
		expect(sameNames([{ name: 'a', id: 1 }], [{ name: 'a', id: 2 }])).toBe(true);
		expect(sameNames([{ name: 'a' }, { name: 'b' }], [{ name: 'b' }, { name: 'a' }])).toBe(false);
	});

	it('Finds lists of different lengths different', () => {
		expect(sameNames([{ name: 'a' }], [{ name: 'a' }, { name: 'a' }])).toBe(false);
	});
});
