import {
	buildGeneCopy,
	buildGeneCreatePayload,
	buildGeneDetailPayload,
	gcrpCrossReferenceFieldErrors,
	getPendingGeneFields,
	processGeneErrors,
} from '../utils';
import { geneDetailData } from '../mockData/mockData';

vi.mock('../../../service/ResourceDescriptorService', async () =>
	(await import('./geneServiceMocks')).mockResourceDescriptorService()
);

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

describe('buildGeneCreatePayload', () => {
	it('sends the gene as the detail save does, with the type a new gene is built from', () => {
		const gene = { taxon: { curie: '' }, geneType: { curie: '' }, geneSymbol: { dataKey: 'row', displayText: 'abc' } };

		expect(buildGeneCreatePayload(gene)).toEqual({
			type: 'Gene',
			taxon: undefined,
			geneType: undefined,
			geneSymbol: { dataKey: 'row', displayText: 'abc' },
			gcrpCrossReference: undefined,
		});
	});

	it('sends the GCRP cross reference without the fields its table keeps for itself', () => {
		const page = { id: 7, name: 'default' };
		const gene = {
			geneSymbol: { displayText: 'abc' },
			gcrpCrossReference: {
				dataKey: 'gcrp-row',
				referencedCurie: 'UniProtKB:P91931',
				resourceDescriptor: { id: 3, prefix: 'UniProtKB' },
				resourceDescriptorPage: page,
			},
		};

		expect(buildGeneCreatePayload(gene).gcrpCrossReference).toEqual({
			referencedCurie: 'UniProtKB:P91931',
			resourceDescriptorPage: page,
		});
	});
});

describe('processGeneErrors', () => {
	const gcrpRow = { dataKey: 'gcrp-row', referencedCurie: 'UniProtKB:P91931' };

	it("puts the GCRP cross reference's field errors on its row's cells and keeps the others on the page", () => {
		const dispatch = vi.fn();
		const data = {
			errorMessages: { gcrpCrossReference: 'referencedCurie - Invalid', taxon: 'Required field is empty' },
		};

		processGeneErrors(data, dispatch, { gcrpCrossReference: gcrpRow });

		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'gcrpCrossReference',
			errorMessages: { 'gcrp-row': { referencedCurie: { severity: 'error', message: 'Invalid' } } },
		});
		expect(dispatch).toHaveBeenLastCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { taxon: 'Required field is empty' },
		});
	});

	it('keeps a GCRP cross reference error that names no field with the section', () => {
		const dispatch = vi.fn();

		processGeneErrors({ errorMessages: { gcrpCrossReference: 'Not a valid entry' } }, dispatch, {
			gcrpCrossReference: gcrpRow,
		});

		expect(dispatch).toHaveBeenCalledTimes(1);
		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorMessages: { gcrpCrossReference: 'Not a valid entry' },
		});
	});
});

describe('buildGeneCopy', () => {
	const alwaysVisible = () => true;

	it("copies the fields the create form edits, without the stored gene's ids, audit fields or data provider", async () => {
		const copy = await buildGeneCopy(saved, alwaysVisible);

		expect(copy).not.toHaveProperty('id');
		expect(copy).not.toHaveProperty('curie');
		expect(copy).not.toHaveProperty('primaryExternalId');
		expect(copy).not.toHaveProperty('dataProvider');
		expect(copy.obsolete).toBe(false);
		expect(copy.taxon.curie).toBe(saved.taxon.curie);
		expect(copy.geneType.curie).toBe(saved.geneType.curie);
		['geneSymbol', 'geneFullName', 'geneSystematicName'].forEach((field) => {
			expect(copy[field].displayText).toBe(saved[field].displayText);
			expect(copy[field]).not.toHaveProperty('id');
			expect(copy[field]).not.toHaveProperty('dateCreated');
		});
	});

	it("keeps the GCRP cross reference's page, curie and display name, but not its id", async () => {
		const { gcrpCrossReference } = await buildGeneCopy(saved, alwaysVisible);

		expect(gcrpCrossReference.referencedCurie).toBe(saved.gcrpCrossReference.referencedCurie);
		expect(gcrpCrossReference.displayName).toBe(saved.gcrpCrossReference.displayName);
		expect(gcrpCrossReference).not.toHaveProperty('id');
		expect(gcrpCrossReference.resourceDescriptorPage.id).toBe(saved.gcrpCrossReference.resourceDescriptorPage.id);
		expect(gcrpCrossReference.dataKey).toBeDefined();
	});

	it('leaves out the GCRP cross reference while its section is hidden', async () => {
		const copy = await buildGeneCopy(saved, (field) => field !== 'GCRP Cross Reference');

		expect(copy.gcrpCrossReference).toBeNull();
	});

	it('starts blank terms for a gene without a taxon or gene type', async () => {
		const copy = await buildGeneCopy({ geneSymbol: { displayText: 'abc' } }, alwaysVisible);

		expect(copy.taxon).toEqual({ curie: '' });
		expect(copy.geneType).toEqual({ curie: '' });
		expect(copy.gcrpCrossReference).toBeNull();
		expect(copy.geneFullName).toBeNull();
	});
});
