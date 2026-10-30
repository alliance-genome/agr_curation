import { buildGeneDetailPayload, gcrpCrossReferenceFieldErrors, getPendingGeneFields } from '../utils';
import { geneDetailData } from '../mockData/mockData';

const saved = geneDetailData.entity;

describe('getPendingGeneFields', () => {
	it('reports nothing while there is no saved gene, or when the form matches it', () => {
		expect(getPendingGeneFields({ ...saved, internal: true }, null).size).toBe(0);
		expect(getPendingGeneFields(structuredClone(saved), saved).size).toBe(0);
	});

	it('reports each single-value field and single-object section that differs', () => {
		const gene = {
			...structuredClone(saved),
			taxon: { curie: 'NCBITaxon:6239' },
			geneType: { curie: 'SO:0001263' },
			obsolete: true,
			geneSymbol: { ...saved.geneSymbol, displayText: 'SamDC2' },
			geneSystematicName: null,
			gcrpCrossReference: { ...saved.gcrpCrossReference, referencedCurie: 'UniProtKB:Q00000' },
		};

		expect([...getPendingGeneFields(gene, saved)]).toEqual([
			'taxon',
			'geneType',
			'obsolete',
			'geneSymbol',
			'geneSystematicName',
			'gcrpCrossReference',
		]);
	});

	it('ignores the fields a table keeps for itself', () => {
		const gene = structuredClone(saved);
		gene.geneSymbol.dataKey = 'symbol-row';
		gene.gcrpCrossReference.dataKey = 'gcrp-row';

		expect(getPendingGeneFields(gene, saved).size).toBe(0);
	});

	it('compares a GCRP cross reference with its descriptor seeded the same as one the API returned', () => {
		const gene = structuredClone(saved);
		gene.gcrpCrossReference.resourceDescriptor = {
			...saved.gcrpCrossReference.resourceDescriptorPage.resourceDescriptor,
			resourcePages: [{ id: 41750700, name: 'default' }],
		};

		expect(getPendingGeneFields(gene, saved).size).toBe(0);
	});
});

describe('buildGeneDetailPayload', () => {
	it('sends a cleared taxon or gene type as unset', () => {
		const payload = buildGeneDetailPayload({ ...saved, taxon: { curie: '' }, geneType: { curie: '' } });

		expect(payload.taxon).toBeUndefined();
		expect(payload.geneType).toBeUndefined();
	});

	it('sends the GCRP cross reference without its table fields, leaving the form gene as it is', () => {
		const gene = {
			...saved,
			gcrpCrossReference: { ...saved.gcrpCrossReference, dataKey: 'gcrp-row', resourceDescriptor: { id: 1 } },
		};

		const payload = buildGeneDetailPayload(gene);

		expect(payload.gcrpCrossReference).not.toHaveProperty('dataKey');
		expect(payload.gcrpCrossReference).not.toHaveProperty('resourceDescriptor');
		expect(payload.gcrpCrossReference.referencedCurie).toBe('UniProtKB:P91931');
		expect(gene.gcrpCrossReference.dataKey).toBe('gcrp-row');
	});

	it('keeps a gene without a GCRP cross reference without one', () => {
		expect(buildGeneDetailPayload({ ...saved, gcrpCrossReference: null }).gcrpCrossReference).toBeNull();
	});
});

describe('gcrpCrossReferenceFieldErrors', () => {
	it('splits the error into its messages by field', () => {
		expect(gcrpCrossReferenceFieldErrors('displayName - Required field | referencedCurie - Invalid')).toEqual({
			displayName: 'Required field',
			referencedCurie: 'Invalid',
		});
	});

	it('reads none from an error that names no field, a field with no cell, or no error', () => {
		expect(gcrpCrossReferenceFieldErrors('Something went wrong')).toBeNull();
		expect(gcrpCrossReferenceFieldErrors('id - Not a valid entry | referencedCurie - Invalid')).toBeNull();
		expect(gcrpCrossReferenceFieldErrors(undefined)).toBeNull();
	});
});
