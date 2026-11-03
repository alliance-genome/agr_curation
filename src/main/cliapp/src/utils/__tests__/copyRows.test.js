import { copyRow, copyRows } from '../copyRows';

const storedRow = {
	id: 7,
	createdBy: { uniqueId: 'curator1' },
	updatedBy: { uniqueId: 'curator2' },
	dateCreated: '2026-01-01T00:00:00Z',
	dateUpdated: '2026-01-02T00:00:00Z',
	dbDateCreated: '2026-01-01T00:00:00Z',
	dbDateUpdated: '2026-01-02T00:00:00Z',
	displayText: 'abc-1',
	internal: false,
	evidence: [{ id: 11, curie: 'AGRKB:101000000000001' }],
};

describe('copyRow', () => {
	it('drops the id and audit fields and keeps the rest', () => {
		expect(copyRow(storedRow)).toEqual({
			displayText: 'abc-1',
			internal: false,
			evidence: [{ id: 11, curie: 'AGRKB:101000000000001' }],
		});
	});

	it('copies deeply, leaving the stored row untouched', () => {
		const copy = copyRow(storedRow);
		copy.evidence[0].curie = 'AGRKB:101000000000002';

		expect(storedRow.evidence[0].curie).toEqual('AGRKB:101000000000001');
		expect(storedRow.id).toEqual(7);
	});

	it('gives null for no row', () => {
		expect(copyRow(null)).toBeNull();
		expect(copyRow(undefined)).toBeNull();
	});
});

describe('copyRows', () => {
	it('copies each row', () => {
		expect(copyRows([storedRow, { id: 8, displayText: 'abc-2' }])).toEqual([
			copyRow(storedRow),
			{ displayText: 'abc-2' },
		]);
	});

	it('gives undefined for no rows', () => {
		expect(copyRows([])).toBeUndefined();
		expect(copyRows(undefined)).toBeUndefined();
	});
});
