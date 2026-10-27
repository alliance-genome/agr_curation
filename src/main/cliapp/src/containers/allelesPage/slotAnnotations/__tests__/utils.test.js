import { buildSlotAnnotationCopies, prepareSlotAnnotations, stripSlotAnnotation } from '../utils';
import { ALLELE_SUB_RESOURCES } from '../../constants';

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

describe('slot annotation lists isSame', () => {
	const { alleleSynonyms, alleleSecondaryIds, alleleNomenclatureEvents, alleleMutationTypes } = ALLELE_SUB_RESOURCES;
	const { alleleFunctionalImpacts, alleleInheritanceModes } = ALLELE_SUB_RESOURCES;

	it('Reads a blank text field as none, as the API omits it', () => {
		expect(
			alleleSynonyms.isSame(
				[{ id: 1, internal: false, displayText: 'a', formatText: 'a', synonymUrl: '' }],
				[{ id: 1, internal: false, displayText: 'a', formatText: 'a' }]
			)
		).toBe(true);
		expect(alleleSecondaryIds.isSame([{ secondaryId: '' }], [{}])).toBe(true);
	});

	it('Finds an edited field, internal flag or evidence', () => {
		const stored = [{ id: 1, internal: false, displayText: 'a', formatText: 'a', evidence: [{ curie: 'AGRKB:1' }] }];

		expect(alleleSynonyms.isSame([{ ...stored[0], displayText: 'b' }], stored)).toBe(false);
		expect(alleleSynonyms.isSame([{ ...stored[0], internal: true }], stored)).toBe(false);
		expect(alleleSynonyms.isSame([{ ...stored[0], evidence: [] }], stored)).toBe(false);
		expect(alleleSynonyms.isSame([{ ...stored[0] }], stored)).toBe(true);
	});

	it('Compares a nomenclature event by its event and obsolete flag', () => {
		const stored = [{ id: 1, nomenclatureEvent: { id: 3, name: 'data_merged' }, obsolete: false }];

		expect(
			alleleNomenclatureEvents.isSame([{ ...stored[0], nomenclatureEvent: { name: 'data_merged' } }], stored)
		).toBe(true);
		expect(alleleNomenclatureEvents.isSame([{ ...stored[0], obsolete: true }], stored)).toBe(false);
	});

	it('Compares mutation types and functional impacts in any order', () => {
		expect(
			alleleMutationTypes.isSame(
				[{ mutationTypes: [{ curie: 'SO:2' }, { curie: 'SO:1' }] }],
				[{ mutationTypes: [{ curie: 'SO:1' }, { curie: 'SO:2' }] }]
			)
		).toBe(true);
		expect(
			alleleFunctionalImpacts.isSame(
				[{ functionalImpacts: [{ name: 'b' }, { name: 'a' }] }],
				[{ functionalImpacts: [{ name: 'a' }, { name: 'b' }] }]
			)
		).toBe(true);
	});

	it('Reads a typed phenotype term as the curie typed, the same as the picked term', () => {
		const stored = [{ id: 1, inheritanceMode: { name: 'dominant' }, phenotypeTerm: { curie: 'MP:1', name: 'p' } }];

		expect(alleleInheritanceModes.isSame([{ ...stored[0], phenotypeTerm: { curie: 'MP:1' } }], stored)).toBe(true);
		expect(alleleInheritanceModes.isSame([{ ...stored[0], phenotypeTerm: { curie: 'MP:2' } }], stored)).toBe(false);
		expect(alleleInheritanceModes.isSame([{ ...stored[0], phenotypeTerm: null }], stored)).toBe(false);
	});

	it('Finds an added or removed row', () => {
		expect(alleleSecondaryIds.isSame([{ secondaryId: 'WB:1' }], [])).toBe(false);
		expect(alleleSecondaryIds.isSame([], [{ id: 1, secondaryId: 'WB:1' }])).toBe(false);
	});
});
