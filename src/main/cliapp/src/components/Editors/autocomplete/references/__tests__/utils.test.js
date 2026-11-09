import { isPickedReference } from '../utils';

describe('isPickedReference', () => {
	it('accepts a reference from the literature search', () => {
		expect(isPickedReference({ curie: 'AGRKB:101', short_citation: 'Doe 2020', cross_references: [] })).toBe(true);
	});

	it('refuses typed text, which the autosuggest holds as a curie alone', () => {
		expect(isPickedReference({ curie: 'AGRKB:999' })).toBe(false);
	});

	it.each([null, undefined])('refuses %s', (reference) => {
		expect(isPickedReference(reference)).toBe(false);
	});

	it('refuses an object without a curie', () => {
		expect(isPickedReference({ short_citation: 'Doe 2020' })).toBe(false);
	});
});
