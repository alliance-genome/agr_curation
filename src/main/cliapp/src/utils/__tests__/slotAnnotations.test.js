import { buildSlotAnnotationCopies, prepareSlotAnnotations, stripSlotAnnotation } from '../slotAnnotations';

describe('prepareSlotAnnotations', () => {
	it('Gives each row its own dataKey without changing the rows it is given', () => {
		const stored = [
			{ id: 1, secondaryId: 'WB:1' },
			{ id: 2, secondaryId: 'WB:2' },
		];

		const rows = prepareSlotAnnotations(stored);

		expect(rows.map((row) => row.secondaryId)).toEqual(['WB:1', 'WB:2']);
		expect(new Set(rows.map((row) => row.dataKey)).size).toBe(2);
		expect(stored[0]).not.toHaveProperty('dataKey');
	});

	it('Reads no list as none', () => {
		expect(prepareSlotAnnotations(undefined)).toEqual([]);
	});
});

describe('stripSlotAnnotation', () => {
	it('Drops the dataKey and the allele, and keeps the id', () => {
		expect(stripSlotAnnotation({ id: 5, dataKey: 'k', singleAllele: { id: 1 }, secondaryId: 'WB:1' })).toEqual({
			id: 5,
			secondaryId: 'WB:1',
		});
	});
});

describe('buildSlotAnnotationCopies', () => {
	it('Copies each row without its id or audit fields', () => {
		const [copy] = buildSlotAnnotationCopies([
			{ id: 5, dateCreated: '2024-01-01', createdBy: { id: 2 }, internal: true, nomenclatureEvent: { name: 'x' } },
		]);

		expect(copy).not.toHaveProperty('id');
		expect(copy).not.toHaveProperty('dateCreated');
		expect(copy).not.toHaveProperty('createdBy');
		expect(copy).toMatchObject({ internal: true, nomenclatureEvent: { name: 'x' } });
		expect(copy.dataKey).toBeDefined();
	});
});
